package se.skltp.tak.web.security.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DpopProofValidator")
class DpopProofValidatorTest {

    private static final String HTM = "GET";
    private static final String HTU = "https://api.example.com/resource";
    private static final String ACCESS_TOKEN = "eyJhbGciOiJSUzI1NiJ9.fake.token";

    private ECKey ecKey;
    private DpopProofValidator validator;

    @BeforeEach
    void setUp() throws JOSEException {
        ecKey     = new ECKeyGenerator(Curve.P_256).generate();
        validator = new DpopProofValidator();
    }

    // ── happy path ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("accepts a fully valid DPoP proof (without ath)")
    void shouldAcceptValidProofWithoutAth() throws Exception {
        String proof = buildProof(ecKey, HTM, HTU, null, Instant.now(), UUID.randomUUID().toString());
        assertThatCode(() -> validator.validate(proof, HTM, HTU, null)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("accepts a fully valid DPoP proof with correct ath")
    void shouldAcceptValidProofWithAth() throws Exception {
        String ath   = sha256Base64url(ACCESS_TOKEN);
        String proof = buildProof(ecKey, HTM, HTU, ath, Instant.now(), UUID.randomUUID().toString());
        assertThatCode(() -> validator.validate(proof, HTM, HTU, ACCESS_TOKEN)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("returns the JWK thumbprint on success")
    void shouldReturnJkt() throws Exception {
        String proof  = buildProof(ecKey, HTM, HTU, null, Instant.now(), UUID.randomUUID().toString());
        String jkt    = validator.validate(proof, HTM, HTU, null);
        String expected = ecKey.computeThumbprint().toString();
        assertThat(jkt).isEqualTo(expected);
    }

    // ── typ ─────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("rejects proof with wrong typ")
    void shouldRejectWrongTyp() throws Exception {
        String proof = buildProofWithTyp(ecKey, "JWT");
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("typ=dpop+jwt");
    }

    // ── htm / htu ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("rejects proof when htm does not match the request method")
    void shouldRejectWrongHtm() throws Exception {
        String proof = buildProof(ecKey, "POST", HTU, null, Instant.now(), UUID.randomUUID().toString());
        assertThatThrownBy(() -> validator.validate(proof, "GET", HTU, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("htm mismatch");
    }

    @Test
    @DisplayName("rejects proof when htu does not match the request URI")
    void shouldRejectWrongHtu() throws Exception {
        String proof = buildProof(ecKey, HTM, "https://other.example.com/", null, Instant.now(), UUID.randomUUID().toString());
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("htu mismatch");
    }

    @Test
    @DisplayName("accepts htu with query string — normalizes before comparing")
    void shouldNormalizeHtuForComparison() throws Exception {
        // proof has clean htu, request URI has a query string — normalization should make them equal
        String proof = buildProof(ecKey, HTM, HTU, null, Instant.now(), UUID.randomUUID().toString());
        assertThatCode(() -> validator.validate(proof, HTM, HTU + "?q=1", null)).doesNotThrowAnyException();
    }

    // ── iat ──────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("rejects proof whose iat is too old (> 60 s)")
    void shouldRejectExpiredProof() throws Exception {
        Instant tooOld = Instant.now().minusSeconds(61);
        String proof = buildProof(ecKey, HTM, HTU, null, tooOld, UUID.randomUUID().toString());
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("iat");
    }

    @Test
    @DisplayName("rejects proof whose iat is in the future (> clock skew)")
    void shouldRejectFutureProof() throws Exception {
        Instant future = Instant.now().plusSeconds(10);
        String proof = buildProof(ecKey, HTM, HTU, null, future, UUID.randomUUID().toString());
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("iat");
    }

    // ── jti ──────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("rejects a replayed proof (same jti used twice)")
    void shouldRejectReplayedJti() throws Exception {
        String jti   = UUID.randomUUID().toString();
        String proof = buildProof(ecKey, HTM, HTU, null, Instant.now(), jti);

        // First use: must succeed
        validator.validate(proof, HTM, HTU, null);

        // Second use of the same proof: must be rejected
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jti");
    }

    @Test
    @DisplayName("rejects proof with missing jti")
    void shouldRejectMissingJti() throws Exception {
        String proof = buildProofWithoutJti(ecKey);
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("jti");
    }

    // ── ath ──────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("rejects proof with incorrect ath (token binding broken)")
    void shouldRejectWrongAth() throws Exception {
        String wrongAth = sha256Base64url("different.token.value");
        String proof = buildProof(ecKey, HTM, HTU, wrongAth, Instant.now(), UUID.randomUUID().toString());
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, ACCESS_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ath");
    }

    @Test
    @DisplayName("rejects proof missing ath when access token is provided")
    void shouldRejectMissingAth() throws Exception {
        String proof = buildProof(ecKey, HTM, HTU, null, Instant.now(), UUID.randomUUID().toString());
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, ACCESS_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ath");
    }

    // ── signature / key ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("rejects proof signed by a different key than the embedded JWK")
    void shouldRejectSignatureMismatch() throws Exception {
        // Build a proof where the header JWK is from key1 but the signature is from key2
        ECKey key2   = new ECKeyGenerator(Curve.P_256).generate();
        String proof = buildProofWithMismatchedKey(ecKey, key2);
        assertThatThrownBy(() -> validator.validate(proof, HTM, HTU, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("signature");
    }

    // ── builder helpers ──────────────────────────────────────────────────────────

    private String buildProof(ECKey key, String htm, String htu, String ath,
                              Instant iat, String jti) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(new JOSEObjectType("dpop+jwt"))
                .jwk(key.toPublicJWK())
                .build();

        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .jwtID(jti)
                .claim("htm", htm)
                .claim("htu", htu)
                .issueTime(Date.from(iat));
        if (ath != null) claims.claim("ath", ath);

        SignedJWT jwt = new SignedJWT(header, claims.build());
        jwt.sign(new ECDSASigner(key));
        return jwt.serialize();
    }

    private String buildProofWithTyp(ECKey key, String typ) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(new JOSEObjectType(typ))
                .jwk(key.toPublicJWK())
                .build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .claim("htm", HTM)
                .claim("htu", HTU)
                .issueTime(new Date())
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new ECDSASigner(key));
        return jwt.serialize();
    }

    private String buildProofWithoutJti(ECKey key) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(new JOSEObjectType("dpop+jwt"))
                .jwk(key.toPublicJWK())
                .build();

        // jwtID deliberately omitted
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .claim("htm", HTM)
                .claim("htu", HTU)
                .issueTime(new Date())
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new ECDSASigner(key));
        return jwt.serialize();
    }

    /** Header JWK = key1 public, but signed with key2 → signature invalid. */
    private String buildProofWithMismatchedKey(ECKey embeddedKey, ECKey signingKey) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(new JOSEObjectType("dpop+jwt"))
                .jwk(embeddedKey.toPublicJWK())   // advertise key1
                .build();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .claim("htm", HTM)
                .claim("htu", HTU)
                .issueTime(new Date())
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new ECDSASigner(signingKey));     // sign with key2 → mismatch
        return jwt.serialize();
    }

    private String sha256Base64url(String value) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}

