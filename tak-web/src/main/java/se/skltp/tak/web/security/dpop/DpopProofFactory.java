package se.skltp.tak.web.security.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * Creates DPoP proof JWTs (RFC 9449) signed with the server's EC keypair.
 *
 * <h3>Usage</h3>
 * <pre>
 *   // Token endpoint — no access token yet
 *   String proof = proofFactory.createProof("POST", "https://kc/realms/r/protocol/openid-connect/token");
 *
 *   // Resource server call — include ath
 *   String proof = proofFactory.createProof("GET", "https://api/resource", accessToken);
 * </pre>
 */
@Component
public class DpopProofFactory {

    private static final String DPOP_JWT_TYPE = "dpop+jwt";

    private final DpopKeyManager keyManager;

    public DpopProofFactory(DpopKeyManager keyManager) {
        this.keyManager = keyManager;
    }

    /**
     * Creates a DPoP proof for a token endpoint request (no {@code ath} claim).
     *
     * @param htm HTTP method, e.g. {@code "POST"}
     * @param htu Target URI without query string or fragment
     */
    public String createProof(String htm, String htu) throws JOSEException {
        return createProof(htm, htu, null);
    }

    /**
     * Creates a DPoP proof, optionally binding it to an existing access token.
     *
     * @param htm         HTTP method
     * @param htu         Target URI without query string or fragment
     * @param accessToken The raw (encoded) access token to bind via {@code ath}, or {@code null}
     */
    public String createProof(String htm, String htu, String accessToken) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(new JOSEObjectType(DPOP_JWT_TYPE))
                .jwk(keyManager.getPublicKey())   // embed public key only — never the private part
                .build();

        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())   // unique per proof for replay protection
                .claim("htm", htm)
                .claim("htu", normalizeUri(htu))
                .issueTime(new Date());

        if (accessToken != null) {
            // ath = BASE64URL(SHA-256(ASCII(access_token))) per RFC 9449 §4.2
            claims.claim("ath", sha256Base64Url(accessToken));
        }

        SignedJWT signedJWT = new SignedJWT(header, claims.build());
        signedJWT.sign(new ECDSASigner(keyManager.getEcKey()));
        return signedJWT.serialize();
    }

    /** Strips query string and fragment as required by RFC 9449 §4.2. */
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

