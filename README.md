# README.md
## Tjänsteadresseringskatalogen (TAK)
TAK består av tre fristående men samverkande komponenter:

| Modul          | Beskrivning                                | Standardport |
|----------------|--------------------------------------------|--------------|
| **tak-web**    | Web-GUI (Spring MVC + Thymeleaf)           | 8080 |
| **tak-services** | REST/Batch-tjänster + cache              | 8080 |
| **tak-monitor** | Bevakar databasversion & rensar cache     | 8080 |

> **Tips:** Kör du flera moduler lokalt, ändra port i `application.yaml` eller placera allt bakom en reverse proxy som 
> terminerar TLS och routar paths.

---

## Förkrav
| Komponent | Version/krav |
|-----------|--------------|
| **Java JDK** | 17 eller senare |
| **Maven** | ≥ 3.8 |
| **MySQL** | ≥ 8.0 (utf-8mb4) |
| **Docker / Docker Compose** | *(valfritt men rekommenderas)* |

---
# Inloggning/användarhantering

## Klassiskt läge

Inloggning och användarhantering hanteras helt av applikationen själv via **Spring Security** med formulärbaserad inloggning.

| Aspekt                    | Beskrivning                                                                                                                                |
|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| **Autentisering**         | Klassisk formulärinloggning (`/login`) med användarnamn och lösenord. Spring Security validerar mot databasen.                             |
| **Användarkonton**        | Lagras i MySQL-databasen (tabell `Anvandare` / `User`). Lösenord hashas med BCrypt.                                                        |
| **Roller / behörigheter** | Roller (t.ex. `ADMIN`, `USER`) kopplas till användare i databasen och styr åtkomst via Spring Securitys `@Secured`/`hasRole`-annoteringar. |
| **Sessionshantering**     | HTTP-sessioner på servern (JSESSIONID-cookie). Ingen extern token-tjänst.                                                                  |
| **Administrering**        | Administratörer hanterar användarkonton direkt i databasen eller via tak-web:s admin-gränssnitt.                                           |

> **Begränsningar med klassiskt läge:**
> - Ingen Single Sign-On (SSO) — varje modul har sin egen inloggning.
> - Lösenordspolicyer (komplexitet, rotation) måste hanteras manuellt.
> - Ingen centraliserad användarhantering över flera system.
> - Sessionsbaserat — svårare att skala horisontellt utan sticky sessions eller delad sessionslagring.

---
## Inloggning med OIDC OAuth 2.0 och DPoP

TAK stödjer extern autentisering via **Keycloak** med **DPoP** (Demonstration of Proof of Possession, [RFC 9449](https://www.rfc-editor.org/rfc/rfc9449)).  
DPoP binder access-token kryptografiskt till en nyckel som bara servern har — ett stulet token är värdelöst utan den privata nyckeln.

### Så fungerar det

1. **Användaren** surfar till tak-web → omdirigeras till Keycloaks inloggningssida.
2. Efter lyckad inloggning skickar Keycloak tillbaka en **authorization code** till tak-web.
3. tak-web byter koden mot ett **access token** hos Keycloak. I detta anrop bifogas en signerad **DPoP-proof** (JWT). Keycloak binder tokenet till tak-webs publika nyckel via `cnf.jkt`-claimet.
4. tak-web anropar **UserInfo**-endpointen med `Authorization: DPoP <token>` + en ny DPoP-proof.
5. Webbläsaren ser aldrig access-tokenet — den använder bara en vanlig `JSESSIONID`-cookie.

> **Nyckelpar** — tak-web genererar ett EC P-256-nyckelpar vid uppstart. Samma nyckel används för alla DPoP-anrop. Vid omstart skapas ett nytt nyckelpar och användare måste logga in igen.

### Vad behöver du göra?

#### 1. Starta Keycloak

```bash
docker compose up -d keycloak
```

#### 2. Importera klienten i Keycloak

Importera [`takweb-dpop.json`](takweb-dpop.json) i din Keycloak-realm (Clients → Import client). Viktiga inställningar:

| Inställning                  | Värde                                         |
|------------------------------|-----------------------------------------------|
| `publicClient`               | `true`                                        |
| `dpop.bound.access.tokens`   | `true`                                        |
| `pkce.code.challenge.method` | `S256`                                        |
| `redirectUris`               | `http://localhost:8001/*` (anpassa per miljö) |

> **Keycloak 26+** krävs för DPoP-stöd.

#### 3. Konfigurera tak-web

Lägg till följande i `application.properties` (eller motsvarande YAML):

```properties
spring.security.oauth2.client.registration.keycloak.client-id=takweb-dpop
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.scope=openid,profile,email
spring.security.oauth2.client.registration.keycloak.client-authentication-method=none
spring.security.oauth2.client.registration.keycloak.redirect-uri=
spring.security.oauth2.client.provider.keycloak.end-session-uri=
keycloak.post-logout-redirect-uri=

spring.security.oauth2.client.provider.keycloak.authorization-uri=http://localhost:8080/realms/takweb/protocol/openid-connect/auth
spring.security.oauth2.client.provider.keycloak.token-uri=http://host.docker.internal:8080/realms/takweb/protocol/openid-connect/token
spring.security.oauth2.client.provider.keycloak.jwk-set-uri=http://host.docker.internal:8080/realms/takweb/protocol/openid-connect/certs
spring.security.oauth2.client.provider.keycloak.user-info-uri=http://host.docker.internal:8080/realms/takweb/protocol/openid-connect/userinfo
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username

dpop.validation.enabled=false
```

> **`authorization-uri`** pekar på den URL webbläsaren ser (t.ex. `localhost`), medan `token-uri`/`jwk-set-uri`/`user-info-uri` pekar dit servern når Keycloak (t.ex. `host.docker.internal` i Docker).

#### 4. Starta och verifiera

```bash
mvn package -pl tak-web -DskipTests
docker compose up -d tak-web
```

I loggen ska du se:
```
INFO  DpopKeyManager — DPoP: generated EC P-256 keypair, kid=...
```

Efter inloggning visas en **DPoP ✅**-badge i tak-web. Access-tokenet innehåller `cnf.jkt` som matchar serverns nyckel.

### Mer information

Se [docs/dpop-implementation.md](docs/dpop-implementation.md) för fullständig teknisk dokumentation, sekvensdiagram, implementationsdetaljer och felsökning.

---

## Bygga
```shell
mvn clean package
# --> target/tak-<modul>-4.0.12.jar
