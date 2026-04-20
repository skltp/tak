package se.skltp.tak.web.security.dpop;

import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.RestClientRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;

/**
 * Wraps Spring Security's OAuth2 token clients to attach a fresh {@code DPoP} proof header
 * to every token request sent to Keycloak.
 *
 * <p>Uses the non-deprecated Spring Security 6.4+ {@code RestClient}-based clients:
 * {@link RestClientAuthorizationCodeTokenResponseClient} for the authorization-code flow and
 * {@link RestClientRefreshTokenTokenResponseClient} for token refresh.
 *
 * <p>Wire into {@code SecurityConfig}:
 * <pre>
 *   .oauth2Login(o -> o
 *       .tokenEndpoint(t -> t
 *           .accessTokenResponseClient(dpopTokenResponseClient.authorizationCode())
 *       )
 *   )
 * </pre>
 */
@Component
public class DpopTokenResponseClient {

    private static final Logger log = LoggerFactory.getLogger(DpopTokenResponseClient.class);

    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCode;
    private final OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest>      refreshToken;

    public DpopTokenResponseClient(DpopProofFactory proofFactory) {
        // IMPORTANT: the RestClient must have OAuth2AccessTokenResponseHttpMessageConverter
        // so that the token response (including id_token in additionalParameters) is
        // parsed correctly. Without it, OidcAuthorizationCodeAuthenticationProvider
        // receives a response with additionalParameters=null and throws NullPointerException.
        RestClient restClient = RestClient.builder()
                .messageConverters(converters -> {
                    converters.clear();
                    converters.add(new FormHttpMessageConverter());
                    converters.add(new OAuth2AccessTokenResponseHttpMessageConverter());
                })
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (req, resp) -> {
                            throw new OAuth2AuthorizationException(
                                    new OAuth2Error("token_endpoint_error",
                                            "Token endpoint returned HTTP " + resp.getStatusCode(), null));
                        })
                .requestInterceptor(new DpopHeaderInterceptor(proofFactory))
                .build();

        RestClientAuthorizationCodeTokenResponseClient authCodeClient =
                new RestClientAuthorizationCodeTokenResponseClient();
        authCodeClient.setRestClient(restClient);
        this.authorizationCode = authCodeClient;

        RestClientRefreshTokenTokenResponseClient refreshClient =
                new RestClientRefreshTokenTokenResponseClient();
        refreshClient.setRestClient(restClient);
        this.refreshToken = refreshClient;
    }

    /** Client used during the OIDC authorization-code flow. */
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCode() {
        return authorizationCode;
    }

    /** Client used when Spring Security silently refreshes an expired access token. */
    public OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshToken() {
        return refreshToken;
    }

    // ── DPoP interceptor ─────────────────────────────────────────────────────────

    /**
     * {@link ClientHttpRequestInterceptor} that signs a fresh DPoP proof for each token
     * request. No {@code ath} claim is needed at the token endpoint.
     */
    private static class DpopHeaderInterceptor implements ClientHttpRequestInterceptor {

        private final DpopProofFactory proofFactory;

        DpopHeaderInterceptor(DpopProofFactory proofFactory) {
            this.proofFactory = proofFactory;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request,
                                            byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            String htm = request.getMethod().name();
            // Strip query string — token endpoint URI must not include it per RFC 9449
            String htu = request.getURI().toString();
            int q = htu.indexOf('?');
            if (q > 0) htu = htu.substring(0, q);

            try {
                String proof = proofFactory.createProof(htm, htu);
                request.getHeaders().add("DPoP", proof);
                log.info("DPoP: attached proof for {} {} — token exchange will produce a cnf.jkt-bound access token", htm, htu);
            } catch (JOSEException e) {
                throw new IOException("Failed to create DPoP proof for token request to " + htu, e);
            }

            return execution.execute(request, body);
        }
    }
}
