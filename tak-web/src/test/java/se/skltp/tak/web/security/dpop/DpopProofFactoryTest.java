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

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DpopProofFactory")
class DpopProofFactoryTest {

    private DpopKeyManager keyManager;
    private DpopProofFactory factory;

    @BeforeEach
    void setUp() throws JOSEException {
        keyManager = new DpopKeyManager();
        factory    = new DpopProofFactory(keyManager);
    }

    @Test
    @DisplayName("creates a valid compact-serialized DPoP proof JWT")
    void shouldCreateValidProof() throws Exception {
        String proof = factory.createProof("POST", "https://kc/realms/tak/protocol/openid-connect/token");

        assertThat(proof).isNotBlank();
        // compact serialization: header.payload.signature
        assertThat(proof.split("\\.")).hasSize(3);

        SignedJWT jwt = SignedJWT.parse(proof);
        assertThat(jwt.getHeader().getType().getType()).isEqualToIgnoringCase("dpop+jwt");
        assertThat(jwt.getHeader().getAlgorithm()).isEqualTo(JWSAlgorithm.ES256);
        assertThat(jwt.getJWTClaimsSet().getStringClaim("htm")).isEqualTo("POST");
        assertThat(jwt.getJWTClaimsSet().getStringClaim("htu"))
                .isEqualTo("https://kc/realms/tak/protocol/openid-connect/token");
        assertThat(jwt.getJWTClaimsSet().getJWTID()).isNotBlank();
        assertThat(jwt.getJWTClaimsSet().getIssueTime()).isNotNull();
    }

    @Test
    @DisplayName("strips query string from htu")
    void shouldNormalizeHtu() throws Exception {
        String proof = factory.createProof("GET", "https://api/resource?foo=bar");
        SignedJWT jwt = SignedJWT.parse(proof);
        assertThat(jwt.getJWTClaimsSet().getStringClaim("htu")).isEqualTo("https://api/resource");
    }

    @Test
    @DisplayName("includes ath claim when access token is provided")
    void shouldIncludeAth() throws Exception {
        String accessToken = "some.access.token";
        String proof = factory.createProof("GET", "https://api/res", accessToken);
        SignedJWT jwt = SignedJWT.parse(proof);

        String ath = jwt.getJWTClaimsSet().getStringClaim("ath");
        assertThat(ath).isNotBlank();
        // ath must be the base64url SHA-256 of the access token
        assertThat(ath).doesNotContain("=").doesNotContain("+").doesNotContain("/");
    }

    @Test
    @DisplayName("omits ath claim when no access token is given")
    void shouldOmitAthWithoutToken() throws Exception {
        String proof = factory.createProof("POST", "https://kc/token");
        SignedJWT jwt = SignedJWT.parse(proof);
        assertThat(jwt.getJWTClaimsSet().getClaim("ath")).isNull();
    }

    @Test
    @DisplayName("each proof has a unique jti")
    void shouldGenerateUniqueJti() throws Exception {
        String proof1 = factory.createProof("GET", "https://api/r");
        String proof2 = factory.createProof("GET", "https://api/r");

        String jti1 = SignedJWT.parse(proof1).getJWTClaimsSet().getJWTID();
        String jti2 = SignedJWT.parse(proof2).getJWTClaimsSet().getJWTID();

        assertThat(jti1).isNotEqualTo(jti2);
    }

    @Test
    @DisplayName("embedded JWK contains no private key material")
    void shouldEmbedOnlyPublicKey() throws Exception {
        String proof = factory.createProof("GET", "https://api/r");
        SignedJWT jwt = SignedJWT.parse(proof);
        assertThat(jwt.getHeader().getJWK().isPrivate()).isFalse();
    }

    @Test
    @DisplayName("proof can be verified with the manager's public key")
    void shouldBeVerifiableWithPublicKey() throws Exception {
        String proof = factory.createProof("GET", "https://api/r");
        SignedJWT jwt = SignedJWT.parse(proof);

        com.nimbusds.jose.JWSVerifier verifier =
                new com.nimbusds.jose.crypto.ECDSAVerifier(keyManager.getPublicKey());
        assertThat(jwt.verify(verifier)).isTrue();
    }
}

