package se.skltp.tak.web.security.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Manages the server-side DPoP EC keypair (P-256 / ES256).
 *
 * <p>A single keypair is generated at startup and reused for the lifetime of the
 * server instance. This is the standard approach for a server-rendered OAuth2 client
 * where the server is the DPoP sender — the same key must be used both when obtaining
 * the token from Keycloak and when calling downstream resource servers with it.
 *
 * <p>In a clustered deployment every node will have its own keypair. Sticky sessions
 * or an external key store (e.g. Azure Key Vault, AWS KMS) are then recommended so that
 * the token-issuance request and all subsequent API calls are handled by the same node.
 */
@Component
public class DpopKeyManager {

    private static final Logger log = LoggerFactory.getLogger(DpopKeyManager.class);

    private final ECKey ecKey;

    public DpopKeyManager() {
        try {
            this.ecKey = new ECKeyGenerator(Curve.P_256)
                    .keyID(UUID.randomUUID().toString())
                    .keyUse(KeyUse.SIGNATURE)
                    .generate();
            log.info("DPoP: generated EC P-256 keypair, kid={}", ecKey.getKeyID());
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate DPoP EC keypair", e);
        }
    }

    /** Full key pair — never expose outside this package. */
    ECKey getEcKey() {
        return ecKey;
    }

    /** Public-key-only JWK — safe to embed in DPoP proof headers. */
    public ECKey getPublicKey() {
        return ecKey.toPublicJWK();
    }
}

