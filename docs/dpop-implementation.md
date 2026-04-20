# DPoP Implementation Guide

> **Scope:** This document describes how DPoP (Demonstration of Proof of Possession,
> [RFC 9449](https://www.rfc-editor.org/rfc/rfc9449)) was implemented in **tak-web**,
> a server-rendered Spring Boot / Thymeleaf application that authenticates users via Keycloak.
> A developer should be able to reproduce the same pattern in any Spring Boot app acting
> as an OAuth2 client against a DPoP-capable Keycloak realm.

---

## Table of Contents

1. [What DPoP Is and Why It Matters](#1-what-dpop-is-and-why-it-matters)
2. [Architecture Overview](#2-architecture-overview)
3. [Request Flow Diagrams](#3-request-flow-diagrams)
4. [Keycloak Configuration](#4-keycloak-configuration)
5. [Spring Boot Implementation](#5-spring-boot-implementation)
   - 5.1 [Dependencies](#51-dependencies)
   - 5.2 [DpopKeyManager](#52-dpopkeymanager)
   - 5.3 [DpopProofFactory](#53-dpopprooffactory)
   - 5.4 [DpopTokenResponseClient](#54-dpoptokenresponseclient)
   - 5.5 [DpopOidcUserService](#55-dpopoidcuserservice)
   - 5.6 [DpopProofValidator](#56-dpopproofvalidator)
   - 5.7 [DpopValidationFilter](#57-dpopvalidationfilter)
   - 5.8 [SecurityConfig wiring](#58-securityconfig-wiring)
6. [Configuration Properties](#6-configuration-properties)
7. [Verifying It Works](#7-verifying-it-works)
8. [Pitfalls Encountered](#8-pitfalls-encountered)
9. [Reproducing in Another Spring Boot App](#9-reproducing-in-another-spring-boot-app)
10. [Clustering Considerations](#10-clustering-considerations)
11. [Testing Strategy](#11-testing-strategy)

---

## 1. What DPoP Is and Why It Matters

A plain **Bearer token** can be replayed by anyone who intercepts it — they just copy the
`Authorization: Bearer <token>` header and make requests as the original user.

**DPoP** (Demonstration of Proof of Possession) prevents this by cryptographically binding
the access token to a specific key pair controlled by the legitimate client. The access token
contains a `cnf.jkt` claim (confirmation / JWK thumbprint). Every request that uses the token
must also include a freshly signed `DPoP` proof JWT. The resource server verifies that:

- the proof is signed by the key whose thumbprint is in `cnf.jkt`, and
- the proof covers the exact HTTP method + URI of the current request.

A stolen token is therefore useless without the corresponding private key.

---

## 2. Architecture Overview

### Application type

tak-web is a **server-rendered Thymeleaf app** (not a SPA). The browser never sees the access
token — it only holds a `JSESSIONID` cookie. The **server** is the OAuth2 client and the DPoP
sender. This means:

- The DPoP key pair lives on the **server** (not in the browser).
- A single EC P-256 key pair is generated at startup and reused for the lifetime of the
  server process.
- The same key pair is used for all three DPoP-sensitive HTTP calls: token exchange,
  UserInfo, and downstream API calls.

### Component map

```
tak-web (Spring Boot)
│
├── security/dpop/
│   ├── DpopKeyManager          — generates & holds the EC keypair (singleton)
│   ├── DpopProofFactory        — creates signed dpop+jwt proofs on demand
│   ├── DpopTokenResponseClient — injects DPoP header into token endpoint calls
│   ├── DpopOidcUserService     — injects DPoP header into UserInfo calls
│   ├── DpopProofValidator      — validates incoming DPoP proofs (resource-server side)
│   └── DpopValidationFilter    — servlet filter enforcing DPoP on inbound requests
│
└── configuration/
    └── SecurityConfig          — wires all DPoP components into Spring Security
```

---

## 3. Request Flow Diagrams

### 3.1 Login (authorization-code + token exchange)

```
Browser                  tak-web (server)              Keycloak
  │                           │                            │
  │  GET /tak-web             │                            │
  │──────────────────────────>│                            │
  │                           │  redirect →                │
  │<──────────────────────────│  /oauth2/authorization/keycloak
  │                           │                            │
  │  GET /realms/.../auth     │                            │
  │  ?client_id=takweb-dpop   │                            │
  │  &code_challenge=... (PKCE)───────────────────────────>│
  │<──────────────────────────────────── login form ───────│
  │                           │                            │
  │  POST /realms/.../login   │                            │
  │  (credentials)────────────────────────────────────────>│
  │<────────────────────────────── redirect: ?code=... ────│
  │                           │                            │
  │  GET /login/oauth2/code/keycloak?code=...              │
  │──────────────────────────>│                            │
  │                           │  POST /realms/.../token    │
  │                           │  DPoP: <proof>  ──────────>│  ← DpopTokenResponseClient
  │                           │  (htm=POST, htu=token_url) │
  │                           │<── access_token            │
  │                           │    cnf.jkt = <thumbprint>  │  ← token is now DPoP-bound
  │                           │                            │
  │                           │  GET /realms/.../userinfo  │
  │                           │  Authorization: DPoP <AT>  │
  │                           │  DPoP: <proof> ───────────>│  ← DpopOidcUserService
  │                           │  (htm=GET, htu=userinfo_url,
  │                           │   ath=SHA256(AT))          │
  │                           │<── user claims ────────────│
  │                           │                            │
  │<──── redirect to / ───────│                            │
  │  (JSESSIONID set)         │                            │
```

### 3.2 DPoP Proof JWT structure

Every proof is a compact JWT with:

**Header**
```json
{
  "typ": "dpop+jwt",
  "alg": "ES256",
  "jwk": { "kty": "EC", "crv": "P-256", "x": "...", "y": "..." }
}
```
> ⚠️ The `jwk` field contains the **public key only**. Never include the private key.

**Payload**
```json
{
  "jti": "a unique UUID (replay protection)",
  "htm": "POST",
  "htu": "https://keycloak/realms/takweb/protocol/openid-connect/token",
  "iat": 1713182233,
  "ath": "BASE64URL(SHA-256(access_token))"
}
```
- `htm` / `htu` — bind the proof to the exact HTTP method + URI
- `iat` — must be within 60 s of the server's clock
- `jti` — each proof is one-time use
- `ath` — only required when calling a resource server (not the token endpoint)

### 3.3 Access token cnf claim (issued by Keycloak)

```json
{
  "sub": "f6a9fd42-...",
  "aud": "takweb-dpop",
  "cnf": {
    "jkt": "9Led9kGU50CDUEwZv9SADEM7DHjLET98YD4gRS4eYHU"
  }
}
```
`jkt` = Base64url(SHA-256(public key JWK)) — the thumbprint of the DPoP key pair.

### 3.4 Resource server validation (inbound DPoP)

```
Client                          tak-web / tak-services (RS)
  │                                       │
  │  GET /api/resource                    │
  │  Authorization: DPoP <access_token>   │
  │  DPoP: <proof>  ─────────────────────>│
  │                           DpopValidationFilter:
  │                           1. parse & verify proof signature
  │                           2. check htm / htu / iat / jti / ath
  │                           3. extract jkt from proof's embedded JWK
  │                           4. compare with cnf.jkt in access token
  │                           5. rewrite Authorization: Bearer → downstream JWT filter
  │<─ 200 OK (or 401 invalid_dpop_proof) ─│
```

---

## 4. Keycloak Configuration

### 4.1 Enable DPoP on the client

Import [`takweb-dpop.json`](../takweb-dpop.json) into your Keycloak realm
(Clients → Import client). The critical attribute is:

```json
"attributes": {
  "dpop.bound.access.tokens": "true",
  "pkce.code.challenge.method": "S256"
}
```

With `dpop.bound.access.tokens=true`, Keycloak:
- **Rejects** token requests that don't include a valid `DPoP` header.
- Embeds `cnf.jkt` in every issued access token.
- **Rejects** UserInfo and any other call that uses `Bearer` instead of `DPoP`.

### 4.2 Client settings checklist

| Setting                        | Value                                              |
|--------------------------------|----------------------------------------------------|
| `clientAuthenticatorType`      | `client-secret` (public client — no secret used)   |
| `publicClient`                 | `true`                                             |
| `standardFlowEnabled`          | `true`                                             |
| `directAccessGrantsEnabled`    | `false`                                            |
| `pkce.code.challenge.method`   | `S256`                                             |
| `dpop.bound.access.tokens`     | `true`                                             |
| `redirectUris`                 | `http://localhost:8001/*` (adjust per environment) |

### 4.3 Keycloak version requirement

DPoP is supported in **Keycloak 21+**. The `dpop.bound.access.tokens` attribute was
introduced in Keycloak 21. Verify with:

```bash
curl http://localhost:8080/realms/<realm>/.well-known/openid-configuration \
  | jq '.dpop_signing_alg_values_supported'
# Should return: ["ES256","RS256",...]
```

---

## 5. Spring Boot Implementation

### 5.1 Dependencies

No additional dependencies are required beyond what `spring-boot-starter-oauth2-client`
already pulls in transitively:

| Library                         | Version | How obtained                            |
|---------------------------------|---------|-----------------------------------------|
| `nimbus-jose-jwt`               | 9.40+   | via `spring-security-oauth2-jose`       |
| `spring-security-oauth2-client` | 6.5.x   | via `spring-boot-starter-oauth2-client` |

> **Note:** `RestClientAuthorizationCodeTokenResponseClient` and
> `RestClientRefreshTokenTokenResponseClient` require **Spring Security 6.4+**
> (Spring Boot 3.3+). The older `DefaultAuthorizationCodeTokenResponseClient` is
> deprecated as of 6.4 and should not be used.

`pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<!-- nimbus-jose-jwt is already a transitive dependency — no explicit entry needed -->
```

---

### 5.2 DpopKeyManager

**File:** `se.skltp.tak.web.security.dpop.DpopKeyManager`

Generates a single EC P-256 key pair at application startup and holds it for the lifetime
of the process.

```java
@Component
public class DpopKeyManager {
    private final ECKey ecKey;

    public DpopKeyManager() throws JOSEException {
        this.ecKey = new ECKeyGenerator(Curve.P_256)
                .keyID(UUID.randomUUID().toString())
                .keyUse(KeyUse.SIGNATURE)
                .generate();
    }

    ECKey getEcKey()        { return ecKey; }                 // private key — package-private
    public ECKey getPublicKey() { return ecKey.toPublicJWK(); } // safe to embed in proofs
}
```

**Key decisions:**
- **Singleton per instance** — correct for server-side clients. The same key must be used
  for token acquisition and all subsequent API calls.
- **Ephemeral** — a new key is generated every restart. Sessions from before the restart
  hold tokens bound to a different key and cannot be used (users must log in again).
- **Cluster** — each node has its own key. Use sticky sessions, or externalize the key to
  a shared secret store if cross-node token reuse is required.

---

### 5.3 DpopProofFactory

**File:** `se.skltp.tak.web.security.dpop.DpopProofFactory`

Produces a fresh, signed `dpop+jwt` on demand.

```java
// At the token endpoint (no access token yet — no ath claim)
String proof = proofFactory.createProof("POST", tokenEndpointUri);

// At a resource server (access token exists — include ath)
String proof = proofFactory.createProof("GET", resourceUri, accessToken);
```

**How it builds the proof:**

```
JWSHeader:
  typ  = dpop+jwt
  alg  = ES256
  jwk  = public key only (never expose the private 'd' component)

Payload:
  jti  = UUID.randomUUID()   ← fresh per call, prevents replay
  htm  = e.g. "POST"
  htu  = URI stripped of query string and fragment (RFC 9449 §4.2)
  iat  = current time (seconds)
  ath  = BASE64URL(SHA-256(ASCII(access_token)))   ← only when AT is provided
```

---

### 5.4 DpopTokenResponseClient

**File:** `se.skltp.tak.web.security.dpop.DpopTokenResponseClient`

Wraps Spring Security's token response clients so every call to Keycloak's token endpoint
includes a `DPoP` header.

**Critical detail about message converters:** The `RestClient` passed to
`RestClientAuthorizationCodeTokenResponseClient.setRestClient()` **must** include
`OAuth2AccessTokenResponseHttpMessageConverter`. Without it, the token response is
parsed by Jackson into a malformed object where `additionalParameters` is `null`,
causing a `NullPointerException` in `OidcAuthorizationCodeAuthenticationProvider`
when it looks for `id_token`.

```java
RestClient restClient = RestClient.builder()
    .messageConverters(converters -> {
        converters.clear();
        converters.add(new FormHttpMessageConverter());
        converters.add(new OAuth2AccessTokenResponseHttpMessageConverter()); // ← required
    })
    .defaultStatusHandler(HttpStatusCode::isError, (req, resp) -> {
        throw new OAuth2AuthorizationException(
            new OAuth2Error("token_endpoint_error", "HTTP " + resp.getStatusCode(), null));
    })
    .requestInterceptor(new DpopHeaderInterceptor(proofFactory))
    .build();
```

The interceptor adds the `DPoP` proof header before the request is sent:

```java
// Inside DpopHeaderInterceptor.intercept():
String proof = proofFactory.createProof(htm, htu);  // no ath at token endpoint
request.getHeaders().add("DPoP", proof);
```

Two clients are produced — one for the authorization-code grant and one for refresh:

```java
RestClientAuthorizationCodeTokenResponseClient authCodeClient = ...;
authCodeClient.setRestClient(restClient);
this.authorizationCode = authCodeClient;

RestClientRefreshTokenTokenResponseClient refreshClient = ...;
refreshClient.setRestClient(restClient);
this.refreshToken = refreshClient;
```

---

### 5.5 DpopOidcUserService

**File:** `se.skltp.tak.web.security.dpop.DpopOidcUserService`

Spring Security calls the UserInfo endpoint automatically after the token exchange. The
default `OidcUserService` sends `Authorization: Bearer <token>`. Keycloak rejects this
when the token is DPoP-bound, returning:

```
error="invalid_token"
reason="The access token type is DPoP but Authorization Header is not DPoP"
```

This component extends `OidcUserService` and injects a `ClientHttpRequestInterceptor`
into the underlying `DefaultOAuth2UserService` that:

1. Extracts the access token from `Authorization: Bearer <token>`.
2. Replaces the header with `Authorization: DPoP <token>`.
3. Adds a fresh `DPoP: <proof>` header with `ath` = SHA-256 of the token.

```java
// Inside DpopUserInfoInterceptor.intercept():
String accessToken = authHeader.substring(7); // strip "Bearer "
String proof = proofFactory.createProof(
    request.getMethod().name(),
    userInfoUri,
    accessToken);                  // ath is included here

request.getHeaders().set("Authorization", "DPoP " + accessToken);
request.getHeaders().add("DPoP", proof);
```

> **Note:** `RestClientOAuth2UserService` does not exist in Spring Security 6.5.
> Use `DefaultOAuth2UserService.setRestOperations(RestTemplate)` instead.

---

### 5.6 DpopProofValidator

**File:** `se.skltp.tak.web.security.dpop.DpopProofValidator`

Used on the **resource server side** to validate incoming DPoP proofs. Performs all eight
checks required by RFC 9449:

| Step  | Check                                  | Failure message                        |
|-------|----------------------------------------|----------------------------------------|
| 1     | `typ = dpop+jwt`                       | "DPoP proof must have typ=dpop+jwt"    |
| 2     | Public JWK present, no private key     | "must contain a public JWK"            |
| 3     | Signature valid against embedded key   | "signature verification failed"        |
| 4     | `htm` matches request method           | "htm mismatch"                         |
| 5     | `htu` matches request URI (normalized) | "htu mismatch"                         |
| 6     | `iat` within 60 s window (±5 s skew)   | "iat is outside the acceptable window" |
| 7     | `jti` not seen before                  | "jti has already been used (replay)"   |
| 8     | `ath` matches SHA-256 of access token  | "ath claim does not match"             |

Returns the **JWK thumbprint** (`jkt`) on success so the caller can verify it matches
`cnf.jkt` in the access token.

**Replay cache** — uses a `ConcurrentHashMap<String, Instant>` with lazy eviction
(expired entries are removed on every `put`). This is correct for a single instance.
In a clustered deployment, replace it with a distributed store (Redis, Hazelcast):

```java
// Single-instance (current implementation)
private final ConcurrentMap<String, Instant> jtiCache = new ConcurrentHashMap<>();

// Clustered — example with Redis
// redisTemplate.opsForValue().setIfAbsent(jti, "seen", JTI_TTL, TimeUnit.SECONDS);
```

---

### 5.7 DpopValidationFilter

**File:** `se.skltp.tak.web.security.dpop.DpopValidationFilter`

A `OncePerRequestFilter` (`@Order(HIGHEST_PRECEDENCE + 10)`) that enforces DPoP on
inbound requests. It activates only when the `Authorization` header uses the `DPoP` scheme.

**Flow:**

```
Request arrives
│
├── Authorization: Bearer ... → pass through (non-DPoP request)
│
├── Authorization: DPoP ... and dpop.validation.enabled=false → rewrite to Bearer, pass through
│
└── Authorization: DPoP ... and dpop.validation.enabled=true
    ├── DPoP header missing → 401 invalid_dpop_proof
    ├── DpopProofValidator.validate() fails → 401 invalid_dpop_proof
    ├── cnf.jkt ≠ proof key thumbprint → 401 invalid_dpop_proof
    └── all OK → rewrite Authorization: DPoP → Bearer, continue to JWT filter
```

The `DpopToBearerWrapper` inner class rewrites the `Authorization` header so the
downstream Spring Security `BearerTokenAuthenticationFilter` processes it normally.

**URI reconstruction** — respects `X-Forwarded-Proto` and `X-Forwarded-Host` so that
the computed `htu` matches what the client sent, even behind a Traefik ingress:

```java
String proto = Optional.ofNullable(request.getHeader("X-Forwarded-Proto"))
                       .orElse(request.getScheme());
String host  = Optional.ofNullable(request.getHeader("X-Forwarded-Host"))
                       .orElseGet(() -> request.getServerName() + ":" + request.getServerPort());
return proto + "://" + host + request.getRequestURI();
```

---

### 5.8 SecurityConfig wiring

```java
.oauth2Login(oauth2 -> oauth2
    .defaultSuccessUrl("/", true)
    .tokenEndpoint(token -> token
        // DPoP proof is attached to every auth-code → token exchange
        .accessTokenResponseClient(dpopTokenResponseClient.authorizationCode())
    )
    .userInfoEndpoint(userInfo -> userInfo
        // DPoP proof is attached to the UserInfo call
        .oidcUserService(dpopOidcUserService)
    )
)
```

For resource servers using `oauth2ResourceServer().jwt()`, also add:

```java
http.addFilterBefore(dpopValidationFilter, BearerTokenAuthenticationFilter.class);
```

---

## 6. Configuration Properties

```properties
# ── Keycloak client (must match the imported takweb-dpop.json) ───────────────
spring.security.oauth2.client.registration.keycloak.client-id=takweb-dpop
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.scope=openid,profile,email
spring.security.oauth2.client.registration.keycloak.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
# Public client — no secret, PKCE used automatically
spring.security.oauth2.client.registration.keycloak.client-authentication-method=none

# ── Provider endpoints ────────────────────────────────────────────────────────
# authorization-uri  : browser-facing (redirect target)
# token-uri          : server-to-server (inside Docker: host.docker.internal)
# jwk-set-uri        : server-to-server
# user-info-uri      : server-to-server (used by DpopOidcUserService)
spring.security.oauth2.client.provider.keycloak.authorization-uri=http://localhost:8080/realms/takweb/protocol/openid-connect/auth
spring.security.oauth2.client.provider.keycloak.token-uri=http://host.docker.internal:8080/realms/takweb/protocol/openid-connect/token
spring.security.oauth2.client.provider.keycloak.jwk-set-uri=http://host.docker.internal:8080/realms/takweb/protocol/openid-connect/certs
spring.security.oauth2.client.provider.keycloak.user-info-uri=http://host.docker.internal:8080/realms/takweb/protocol/openid-connect/userinfo
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username

# ── DPoP ─────────────────────────────────────────────────────────────────────
# Set to true on resource servers that receive DPoP-bound tokens.
# Keep false on tak-web itself (browsers authenticate via session cookie).
dpop.validation.enabled=false
```

---

## 7. Verifying It Works

### Server log (immediately after login)

```
INFO  DpopKeyManager         — DPoP: generated EC P-256 keypair, kid=7810f037-...
INFO  DpopTokenResponseClient — DPoP: attached proof for POST http://.../token
INFO  SessionInfoControllerAdvice — DPoP: access token is bound to this server's key — jkt=9Led9kGU...
```

### Browser debug panel (localhost only)

After login, the header shows a green **`DPoP ✅`** badge. Clicking **🔍 Visa token** displays:

```
✅ DPoP ACTIVE — access token is sender-constrained
cnf.jkt     : 9Led9kGU50CDUEwZv9SADEM7DHjLET98YD4gRS4eYHU
server key  : 9Led9kGU50CDUEwZv9SADEM7DHjLET98YD4gRS4eYHU  ← same = correct

=== Access Token Claims ===
cnf: {jkt=9Led9kGU...}
aud: takweb-dpop
...
```

### Manual verification with jwt.io

Paste the access token into [jwt.io](https://jwt.io). The payload must contain:

```json
{
  "cnf": { "jkt": "<thumbprint>" }
}
```

### Negative test

Make a request with a Bearer token instead of DPoP (resource server must have
`dpop.validation.enabled=true`):

```bash
curl -H "Authorization: Bearer <dpop-token>" http://localhost:8001/api/resource
# Expected: HTTP 401
# WWW-Authenticate: DPoP error="invalid_dpop_proof"
```

---

## 8. Pitfalls Encountered

### P1: `NullPointerException` in `OidcAuthorizationCodeAuthenticationProvider`

**Symptom:** Login fails with:
```
Cannot invoke "java.util.Map.containsKey(Object)" because "additionalParameters" is null
  at OidcAuthorizationCodeAuthenticationProvider.authenticate(...:149)
```

**Cause:** The custom `RestClient` used by `RestClientAuthorizationCodeTokenResponseClient`
was built without `OAuth2AccessTokenResponseHttpMessageConverter`. The default converters
(Jackson) cannot deserialize `OAuth2AccessTokenResponse` — it has no public constructor —
so the `additionalParameters` field (which contains `id_token`) ends up `null`.

**Fix:** Explicitly set the converters:
```java
RestClient.builder()
    .messageConverters(converters -> {
        converters.clear();
        converters.add(new FormHttpMessageConverter());
        converters.add(new OAuth2AccessTokenResponseHttpMessageConverter()); // ← key
    })
    ...
```

### P2: Login redirects to `/login?error` — UserInfo rejected by Keycloak

**Symptom:** DPoP proof is sent successfully for the token exchange (confirmed in logs),
but login fails. Keycloak logs show:

```
error="invalid_token"
reason="The access token type is DPoP but Authorization Header is not DPoP"
auth_method="validate_access_token"
```

**Cause:** Spring Security's default `OidcUserService` calls the UserInfo endpoint with
`Authorization: Bearer <token>`. Keycloak rejects this for DPoP-bound tokens.

**Fix:** Replace `OidcUserService` with `DpopOidcUserService`, which rewrites the
`Authorization` header and adds a `DPoP` proof to every UserInfo request.

### P3: `RestClientOAuth2UserService` does not exist in Spring Security 6.5

**Symptom:** Compilation error: `cannot find symbol RestClientOAuth2UserService`.

**Cause:** This class was added in Spring Security 6.6 (Spring Boot 3.4+). It is not
present in 6.5.6.

**Fix:** Use `DefaultOAuth2UserService.setRestOperations(RestTemplate)` instead, which
accepts a `RestTemplate` with a custom interceptor.

### P4: Old Docker image running

**Symptom:** DPoP keypair generation not visible in startup log; token shows
`aud: takweb-client` instead of `aud: takweb-dpop`.

**Cause:** Source code was changed but the Docker image was not rebuilt.

**Fix:**
```bash
mvn package -pl tak-web -DskipTests
docker build -t tak-tak-web tak-web/
docker compose up -d --force-recreate tak-web
```

### P5: Missing PKCE parameters → Keycloak returns `invalid_request`

**Symptom:** Keycloak redirects back with
`error=invalid_request&error_description=Missing+parameter%3A+code_challenge_method`.

**Cause:** The Keycloak client requires PKCE (`pkce.code.challenge.method=S256`) but the
authorization request didn't include `code_challenge`.

**Fix:** Ensure `client-authentication-method=none` is set. Spring Security automatically
enables PKCE for public clients (no client secret).

---

## 9. Reproducing in Another Spring Boot App

To add DPoP to a different Spring Boot app that uses `oauth2Login` against Keycloak:

### Step 1 — Keycloak client

Create a client with `dpop.bound.access.tokens=true` (and `pkce.code.challenge.method=S256`
if public). See §4.

### Step 2 — Copy the `security/dpop` package

Copy these six classes verbatim (they have no project-specific dependencies):

```
DpopKeyManager.java
DpopProofFactory.java
DpopProofValidator.java
DpopTokenResponseClient.java
DpopOidcUserService.java
DpopValidationFilter.java
```

Adjust the package name to suit your project.

### Step 3 — Wire into SecurityConfig

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http,
        DpopTokenResponseClient dpopTokenResponseClient,
        DpopOidcUserService dpopOidcUserService) throws Exception {

    http.oauth2Login(oauth2 -> oauth2
            .tokenEndpoint(t -> t
                .accessTokenResponseClient(dpopTokenResponseClient.authorizationCode()))
            .userInfoEndpoint(u -> u
                .oidcUserService(dpopOidcUserService)));

    // For resource servers, also add:
    // http.addFilterBefore(dpopValidationFilter, BearerTokenAuthenticationFilter.class);

    return http.build();
}
```

### Step 4 — Properties

```properties
spring.security.oauth2.client.registration.<id>.client-id=<your-dpop-client>
spring.security.oauth2.client.registration.<id>.client-authentication-method=none
spring.security.oauth2.client.provider.<id>.user-info-uri=<userinfo-endpoint>
dpop.validation.enabled=false   # set true on resource servers
```

### Step 5 — Verify

Start the app, check the log for:
```
INFO  DpopKeyManager — DPoP: generated EC P-256 keypair, kid=...
```
Then log in and confirm `cnf.jkt` is present in the access token.

---

## 10. Clustering Considerations

| Concern          | Single instance                  | Clustered                      |
|------------------|----------------------------------|--------------------------------|
| Key pair         | Ephemeral, in-memory             | Externalize to Key Vault / KMS |
| JTI replay cache | `ConcurrentHashMap` (in-process) | Redis / Hazelcast              |
| Session affinity | Not required                     | Needed if key is per-instance  |

**Externalizing the key pair** (sketch):

```java
@Bean
public DpopKeyManager dpopKeyManager(KeyVaultClient kv) throws JOSEException {
    // Load or create key from Azure Key Vault / AWS KMS / HashiCorp Vault
    ECKey ecKey = kv.getOrCreateECKey("tak-web-dpop-key");
    return new DpopKeyManager(ecKey);  // custom constructor
}
```

**Shared JTI cache** (sketch using Spring Data Redis):

```java
// In DpopProofValidator.checkJti():
Boolean isNew = redisTemplate.opsForValue()
    .setIfAbsent(jti, "seen", JTI_TTL);
if (!Boolean.TRUE.equals(isNew)) {
    throw new IllegalArgumentException("DPoP jti replayed: " + jti);
}
```

---

## 11. Testing Strategy

### Unit tests

Located in `se.skltp.tak.web.security.dpop.*Test` (21 tests, all passing):

| Test class               | What it covers                                                                                            |
|--------------------------|-----------------------------------------------------------------------------------------------------------|
| `DpopProofFactoryTest`   | Proof structure, htu normalization, ath, jti uniqueness, public-key safety                                |
| `DpopProofValidatorTest` | Valid paths, wrong typ/htm/htu, expired/future iat, replay jti, wrong/missing ath, mismatched signing key |

Run with:
```bash
mvn test -pl tak-web -Dtest="DpopProofFactoryTest,DpopProofValidatorTest"
```

### Integration / manual tests

| Scenario                                      | Expected result                               |
|-----------------------------------------------|-----------------------------------------------|
| Normal login via incognito window             | `DPoP ✅` badge in UI; `cnf.jkt` in token      |
| Replay attack: POST same `DPoP` header twice  | Second request → 401                          |
| Stolen Bearer token on a DPoP-bound resource  | 401 `invalid_dpop_proof`                      |
| Restart server, use old session               | Login again required (token bound to old key) |
| Clock skew `> 60 s` between client and server | 401 (iat outside window)                      |

---

*Document generated 2026-04-15. Covers tak-web v4.4.0-SNAPSHOT, Spring Boot 3.5.7,
Spring Security 6.5.6, Keycloak 26.x, nimbus-jose-jwt 9.40.*

