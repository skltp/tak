package se.skltp.tak.web.security.dpop;

import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Custom {@link OidcUserService} that attaches a DPoP proof to the UserInfo request.
 *
 * <p>When Keycloak issues a DPoP-bound access token, every subsequent use of that token
 * — including the UserInfo call Spring Security makes automatically after the code exchange
 * — must use {@code Authorization: DPoP <token>} plus a fresh {@code DPoP} proof header.
 * Spring Security's default service uses plain Bearer, which Keycloak rejects with
 * {@code "The access token type is DPoP but Authorization Header is not DPoP"}.
 */
@Component
public class DpopOidcUserService extends OidcUserService {

    private static final Logger log = LoggerFactory.getLogger(DpopOidcUserService.class);

    public DpopOidcUserService(DpopProofFactory proofFactory,
                               @Value("${spring.security.oauth2.client.provider.keycloak.user-info-uri}")
                               String userInfoUri) {

        // DefaultOAuth2UserService uses a plain RestTemplate + Jackson for UserInfo JSON.
        // We keep those defaults and only add our DPoP interceptor.
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        restTemplate.getInterceptors().add(new DpopUserInfoInterceptor(proofFactory, userInfoUri));

        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        delegate.setRestOperations(restTemplate);
        setOauth2UserService(delegate);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        log.info("DPoP: loading user from UserInfo for sub={}", userRequest.getIdToken().getSubject());
        System.err.println("===== DPoP OIDC USER SERVICE CALLED ===== for sub=" + userRequest.getIdToken().getSubject());
        return super.loadUser(userRequest);
    }

    // ── interceptor ──────────────────────────────────────────────────────────────

    /**
     * Rewrites every UserInfo request to use the DPoP token scheme:
     * <ol>
     *   <li>Extracts the access token from {@code Authorization: Bearer <token>}
     *   <li>Replaces the header with {@code Authorization: DPoP <token>}
     *   <li>Adds a fresh {@code DPoP} proof ({@code htm=GET}, {@code htu=<userInfoUri>},
     *       {@code ath=SHA-256(token)})
     * </ol>
     */
    private static class DpopUserInfoInterceptor implements ClientHttpRequestInterceptor {

        private final DpopProofFactory proofFactory;
        private final String userInfoUri;

        DpopUserInfoInterceptor(DpopProofFactory proofFactory, String userInfoUri) {
            this.proofFactory = proofFactory;
            int q = userInfoUri.indexOf('?');
            this.userInfoUri = q > 0 ? userInfoUri.substring(0, q) : userInfoUri;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request,
                                            byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {

            String authHeader = request.getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String accessToken = authHeader.substring(7);
                // TEST: Print token for testing purposes
                log.info("===== TEST TOKEN FOR REUSE ===== \nToken: {}", accessToken);
                System.err.println("===== TEST TOKEN FOR REUSE (STDERR) ===== \nToken: " + accessToken);
                try {
                    String proof = proofFactory.createProof(
                            request.getMethod().name(),
                            userInfoUri,
                            accessToken);

                    request.getHeaders().set("Authorization", "DPoP " + accessToken);
                    request.getHeaders().add("DPoP", proof);
                    log.debug("DPoP: attached proof to UserInfo request");

                } catch (JOSEException e) {
                    throw new IOException("Failed to create DPoP proof for UserInfo request", e);
                }
            }

            return execution.execute(request, body);
        }
    }
}
