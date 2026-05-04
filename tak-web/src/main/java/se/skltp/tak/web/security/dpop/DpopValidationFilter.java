package se.skltp.tak.web.security.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

/**
 * Servlet filter that enforces DPoP sender-constraint validation on incoming requests.
 *
 * <h3>What it does</h3>
 * <ol>
 *   <li>Ignores requests that do not carry {@code Authorization: DPoP <token>}.
 *   <li>When DPoP validation is enabled ({@code dpop.validation.enabled=true}):
 *     <ul>
 *       <li>Requires a {@code DPoP} header, validates the proof via {@link DpopProofValidator}.
 *       <li>Verifies that the proof's public-key thumbprint matches {@code cnf.jkt} in the access token.
 *       <li>Returns {@code 401} with {@code WWW-Authenticate: DPoP error="invalid_dpop_proof"} on failure.
 *     </ul>
 *   <li>On success (or when validation is disabled), rewrites the {@code Authorization} header
 *       from {@code "DPoP <token>"} to {@code "Bearer <token>"} so the downstream Spring Security
 *       JWT filter can process it normally.
 * </ol>
 *
 * <h3>Placement in the filter chain</h3>
 * This filter is ordered at {@link Ordered#HIGHEST_PRECEDENCE}{@code  + 10} so it runs before
 * Spring Security's {@code BearerTokenAuthenticationFilter}. When using
 * {@code oauth2ResourceServer().jwt()} in a security chain you should also wire it explicitly:
 * <pre>
 *   http.addFilterBefore(dpopValidationFilter, BearerTokenAuthenticationFilter.class);
 * </pre>
 *
 * <h3>Configuration</h3>
 * <pre>
 *   dpop.validation.enabled=true   # default: false — safe to deploy before resource-server is ready
 * </pre>
 */
@Component
@Profile("oauth-dpop")
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class DpopValidationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(DpopValidationFilter.class);

    private final DpopProofValidator validator;

    /**
     * Master switch. Set to {@code true} on resource servers (e.g. tak-services).
     * Keep {@code false} on tak-web itself (browsers authenticate via OIDC session, not DPoP tokens).
     */
    @Value("${dpop.validation.enabled:false}")
    private boolean dpopValidationEnabled;

    public DpopValidationFilter(@Autowired(required = false) DpopProofValidator validator) {
        this.validator = validator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String dpopHeader = request.getHeader("DPoP");

        // Pass through anything that isn't using the DPoP token scheme
        if (authHeader == null || !authHeader.startsWith("DPoP ")) {
            chain.doFilter(request, response);
            return;
        }

        // DPoP scheme detected — rewrite to Bearer so downstream Spring Security can process it
        if (!dpopValidationEnabled) {
            log.debug("DPoP validation disabled — passing DPoP token as Bearer to downstream filter");
            chain.doFilter(new DpopToBearerWrapper(request), response);
            return;
        }

        if (dpopHeader == null || dpopHeader.isBlank()) {
            rejectWithError(response, "DPoP header is required when using a DPoP-bound access token");
            return;
        }

        String accessToken = authHeader.substring(5); // strip "DPoP "
        String httpMethod  = request.getMethod();
        String httpUri     = reconstructUri(request);

        try {
            // Step 1 — validate the proof (sig, htm, htu, iat, jti, ath)
            String proofJkt = validator.validate(dpopHeader, httpMethod, httpUri, accessToken);

            // Step 2 — verify cnf.jkt token binding
            verifyTokenBinding(accessToken, proofJkt);

            log.debug("DPoP validation passed for {} {}", httpMethod, httpUri);
            chain.doFilter(new DpopToBearerWrapper(request), response);

        } catch (IllegalArgumentException | ParseException e) {
            log.warn("DPoP validation rejected {} {}: {}", httpMethod, httpUri, e.getMessage());
            rejectWithError(response, e.getMessage());
        } catch (JOSEException e) {
            log.warn("DPoP cryptographic error for {} {}: {}", httpMethod, httpUri, e.getMessage());
            rejectWithError(response, "DPoP cryptographic validation error");
        }
    }

    // ── helpers ─────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void verifyTokenBinding(String accessToken, String proofJkt) throws ParseException {
        JWT jwt = JWTParser.parse(accessToken);
        Object cnfClaim = jwt.getJWTClaimsSet().getClaim("cnf");

        if (!(cnfClaim instanceof Map<?, ?> cnf) || !cnf.containsKey("jkt")) {
            throw new IllegalArgumentException(
                    "Access token missing cnf.jkt — it is not a DPoP-bound token");
        }

        String tokenJkt = (String) cnf.get("jkt");
        if (!tokenJkt.equals(proofJkt)) {
            throw new IllegalArgumentException(
                    "DPoP public-key thumbprint (jkt) does not match the cnf.jkt claim in the access token");
        }
    }

    /**
     * Reconstructs the full request URI, respecting {@code X-Forwarded-*} headers set
     * by Traefik / ingress so that the computed URI matches what the client used as {@code htu}.
     */
    private String reconstructUri(HttpServletRequest request) {
        String proto = Optional.ofNullable(request.getHeader("X-Forwarded-Proto"))
                               .orElse(request.getScheme());
        String host  = Optional.ofNullable(request.getHeader("X-Forwarded-Host"))
                               .orElseGet(() -> {
                                   int port = request.getServerPort();
                                   boolean std = (port == 80 && "http".equals(proto))
                                              || (port == 443 && "https".equals(proto));
                                   return request.getServerName() + (std ? "" : ":" + port);
                               });
        return proto + "://" + host + request.getRequestURI();
    }

    private void rejectWithError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate",
                "DPoP error=\"invalid_dpop_proof\", error_description=\"" +
                message.replace("\"", "\\\"") + "\"");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"error\":\"invalid_dpop_proof\",\"error_description\":\"" +
                message.replace("\"", "\\\"") + "\"}");
    }

    /**
     * Rewrites {@code Authorization: DPoP <token>} → {@code Authorization: Bearer <token>}
     * so that the Spring Security JWT filter chain can process the token normally.
     */
    private static class DpopToBearerWrapper extends HttpServletRequestWrapper {

        DpopToBearerWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if ("Authorization".equalsIgnoreCase(name)) {
                String value = super.getHeader(name);
                if (value != null && value.startsWith("DPoP ")) {
                    return "Bearer " + value.substring(5);
                }
            }
            return super.getHeader(name);
        }
    }
}

