package se.skltp.tak.web.security.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Validates incoming DPoP proof JWTs per RFC 9449.
 *
 * <p>Checks performed (in order):
 * <ol>
 *   <li>{@code typ = dpop+jwt}
 *   <li>Embedded public JWK present and not private
 *   <li>Signature valid against embedded key
 *   <li>{@code htm} matches the HTTP method of the request
 *   <li>{@code htu} matches the HTTP URI of the request (after normalization)
 *   <li>{@code iat} within the acceptable freshness window
 *   <li>{@code jti} not seen before (replay protection)
 *   <li>{@code ath} matches SHA-256 of the access token (when {@code accessToken} is non-null)
 * </ol>
 *
 * <p><strong>Clustering note:</strong> The JTI replay cache is in-memory. Replace with a
 * shared store (Redis, Hazelcast, …) in multi-instance deployments.
 */
@Component
public class DpopProofValidator {

    private static final String DPOP_JWT_TYPE      = "dpop+jwt";
    private static final int    MAX_AGE_SECONDS     = 60;
    private static final int    CLOCK_SKEW_SECONDS  = 5;

    private static final Duration MAX_PROOF_AGE = Duration.ofSeconds(MAX_AGE_SECONDS);
    private static final Duration CLOCK_SKEW    = Duration.ofSeconds(CLOCK_SKEW_SECONDS);
    private static final Duration JTI_TTL       = MAX_PROOF_AGE.plus(CLOCK_SKEW);

    /**
     * JTI → expiry-time map. We use {@code putIfAbsent} for atomic replay detection and
     * lazy eviction (expired entries are swept before every put). This avoids an external
     * cache library dependency; replace with Redis in clustered deployments.
     */
    private final ConcurrentMap<String, Instant> jtiCache = new ConcurrentHashMap<>();

    /**
     * Validates a DPoP proof and returns the public-key thumbprint (JKT) of the embedded key.
     *
     * @param dpopHeader  Value of the {@code DPoP} HTTP header
     * @param httpMethod  HTTP method of the request
     * @param httpUri     Full HTTP URI (query string stripped before comparison)
     * @param accessToken Raw encoded access token, or {@code null} at the token endpoint
     * @return Base64url JWK thumbprint of the proof's public key
     * @throws IllegalArgumentException on any validation failure
     * @throws ParseException           if the DPoP header is not a valid compact JWT
     * @throws JOSEException            on cryptographic errors
     */
    public String validate(String dpopHeader, String httpMethod, String httpUri, String accessToken)
            throws ParseException, JOSEException {

        SignedJWT proof  = SignedJWT.parse(dpopHeader);
        JWSHeader header = proof.getHeader();

        checkTyp(header);
        JWK jwk = extractPublicJwk(header);
        verifySignature(proof, jwk);

        JWTClaimsSet claims = proof.getJWTClaimsSet();
        checkHtm(claims, httpMethod);
        checkHtu(claims, httpUri);
        checkIat(claims);
        checkJti(claims);

        if (accessToken != null) {
            checkAth(claims, accessToken);
        }

        return jwk.computeThumbprint().toString();
    }

    // ── individual checks ────────────────────────────────────────────────────────

    private void checkTyp(JWSHeader header) {
        if (header.getType() == null || !DPOP_JWT_TYPE.equalsIgnoreCase(header.getType().getType())) {
            throw new IllegalArgumentException("DPoP proof must have typ=dpop+jwt");
        }
    }

    private JWK extractPublicJwk(JWSHeader header) {
        JWK jwk = header.getJWK();
        if (jwk == null || jwk.isPrivate()) {
            throw new IllegalArgumentException("DPoP proof header must contain a public JWK (no private key)");
        }
        return jwk;
    }

    private void verifySignature(SignedJWT proof, JWK jwk) throws JOSEException {
        JWSVerifier verifier = switch (jwk.getKeyType().getValue()) {
            case "EC"  -> new ECDSAVerifier(jwk.toECKey());
            case "RSA" -> new RSASSAVerifier(jwk.toRSAKey());
            default    -> throw new IllegalArgumentException("Unsupported DPoP key type: " + jwk.getKeyType());
        };
        if (!proof.verify(verifier)) {
            throw new IllegalArgumentException("DPoP proof signature verification failed");
        }
    }

    private void checkHtm(JWTClaimsSet claims, String httpMethod) throws ParseException {
        String htm = claims.getStringClaim("htm");
        if (!httpMethod.equalsIgnoreCase(htm)) {
            throw new IllegalArgumentException("DPoP htm mismatch: expected=" + httpMethod + " got=" + htm);
        }
    }

    private void checkHtu(JWTClaimsSet claims, String httpUri) throws ParseException {
        String htu = claims.getStringClaim("htu");
        if (!normalizeUri(httpUri).equalsIgnoreCase(normalizeUri(htu))) {
            throw new IllegalArgumentException("DPoP htu mismatch: expected=" + httpUri + " got=" + htu);
        }
    }

    private void checkIat(JWTClaimsSet claims) {
        Date iat = claims.getIssueTime();
        if (iat == null) {
            throw new IllegalArgumentException("DPoP proof missing iat claim");
        }
        Instant proofTime = iat.toInstant();
        Instant now       = Instant.now();
        if (proofTime.isBefore(now.minus(MAX_PROOF_AGE)) || proofTime.isAfter(now.plus(CLOCK_SKEW))) {
            throw new IllegalArgumentException(
                    "DPoP proof iat is outside the acceptable window (max age " + MAX_AGE_SECONDS + "s)");
        }
    }

    private void checkJti(JWTClaimsSet claims) {
        String jti = claims.getJWTID();
        if (jti == null || jti.isBlank()) {
            throw new IllegalArgumentException("DPoP proof missing jti claim");
        }
        // Lazy eviction: remove entries whose TTL has elapsed before adding a new one
        Instant now = Instant.now();
        jtiCache.entrySet().removeIf(entry -> entry.getValue().isBefore(now));

        // putIfAbsent: returns null → first use (ok). Returns existing value → replay (reject).
        if (jtiCache.putIfAbsent(jti, now.plus(JTI_TTL)) != null) {
            throw new IllegalArgumentException("DPoP proof jti has already been used (replay): " + jti);
        }
    }

    private void checkAth(JWTClaimsSet claims, String accessToken) throws ParseException {
        String expectedAth = sha256Base64Url(accessToken);
        String actualAth   = claims.getStringClaim("ath");
        if (actualAth == null || !MessageDigest.isEqual(
                expectedAth.getBytes(StandardCharsets.UTF_8),
                actualAth.getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException(
                    "DPoP ath claim does not match access token hash — sender binding broken");
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────────

    private String normalizeUri(String uri) {
        if (uri == null) return "";
        int q = uri.indexOf('?');
        if (q > 0) uri = uri.substring(0, q);
        int f = uri.indexOf('#');
        if (f > 0) uri = uri.substring(0, f);
        return uri;
    }

    private String sha256Base64Url(String value) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}

