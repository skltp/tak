package se.skltp.tak.web.controller;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import se.skltp.tak.web.security.dpop.DpopKeyManager;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;

import static se.skltp.tak.web.util.SecurityUtil.getUserName;

@ControllerAdvice
@Profile("oauth-dpop")
public class SessionInfoControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(SessionInfoControllerAdvice.class);

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final DpopKeyManager dpopKeyManager;

    public SessionInfoControllerAdvice(OAuth2AuthorizedClientService authorizedClientService,
                                       @Autowired(required = false) DpopKeyManager dpopKeyManager) {
        this.authorizedClientService = authorizedClientService;
        this.dpopKeyManager = dpopKeyManager;
    }

    @ModelAttribute
    public void addUserInfoToModel(Model model, HttpServletRequest request) {
        model.addAttribute("username", getUserName());

        boolean isLocalhost = "localhost".equals(request.getServerName())
                || "127.0.0.1".equals(request.getServerName());

        if (!isLocalhost) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return;
        }

        // ── ID token claims (existing behaviour) ──────────────────────────────
        if (auth.getPrincipal() instanceof OidcUser oidcUser) {
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("=== ID Token Claims ===", "");
            claims.putAll(oidcUser.getClaims());
            model.addAttribute("oidcClaims", claims);
        }

        // ── Access token + DPoP binding info ─────────────────────────────────
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            try {
                OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getName());

                if (client != null && client.getAccessToken() != null) {
                    String rawAt = client.getAccessToken().getTokenValue();
                    log.info("DPoP: Access token (for testing/debugging): {}", rawAt);
                    Map<String, Object> atInfo = decodeAccessToken(rawAt);
                    model.addAttribute("accessTokenClaims", atInfo);

                    // DPoP binding check
                    Object cnf = atInfo.get("cnf");
                    String jkt  = extractJkt(cnf);
                    boolean dpopBound = jkt != null;
                    model.addAttribute("dpopBound", dpopBound);

                    if (dpopBound && dpopKeyManager != null) {
                        String serverThumbprint = dpopKeyManager.getPublicKey().computeThumbprint().toString();
                        boolean keyMatch = jkt.equals(serverThumbprint);
                        model.addAttribute("dpopJkt", jkt);
                        model.addAttribute("dpopKeyMatch", keyMatch);
                        model.addAttribute("dpopServerThumbprint", serverThumbprint);

                        if (keyMatch) {
                            log.info("DPoP: access token is bound to this server's key — jkt={}", jkt);
                        } else {
                            log.warn("DPoP: cnf.jkt={} does not match server thumbprint={} — possible key mismatch", jkt, serverThumbprint);
                        }
                    } else {
                        log.warn("DPoP: access token has no cnf.jkt claim — token is NOT DPoP-bound. " +
                                "Check that the Keycloak client has dpop.bound.access.tokens=true and that " +
                                "the DPoP header is being sent during token exchange.");
                    }
                }
            } catch (Exception e) {
                log.debug("DPoP: could not inspect access token: {}", e.getMessage());
            }
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────────

    private Map<String, Object> decodeAccessToken(String rawToken) throws ParseException {
        JWTClaimsSet claims = JWTParser.parse(rawToken).getJWTClaimsSet();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("=== Access Token Claims ===", "");
        result.putAll(claims.getClaims());
        return result;
    }

    @SuppressWarnings("unchecked")
    private String extractJkt(Object cnf) {
        if (cnf instanceof Map<?, ?> cnfMap && cnfMap.containsKey("jkt")) {
            return (String) cnfMap.get("jkt");
        }
        return null;
    }
}
