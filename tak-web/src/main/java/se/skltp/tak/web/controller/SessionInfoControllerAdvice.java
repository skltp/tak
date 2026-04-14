package se.skltp.tak.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.LinkedHashMap;
import java.util.Map;

import static se.skltp.tak.web.util.SecurityUtil.getUserName;

@ControllerAdvice
public class SessionInfoControllerAdvice {

    @ModelAttribute
    public void addUserInfoToModel(Model model, HttpServletRequest request) {
        model.addAttribute("username", getUserName());

        boolean isLocalhost = "localhost".equals(request.getServerName())
                || "127.0.0.1".equals(request.getServerName());

        if (isLocalhost) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof OidcUser oidcUser) {
                Map<String, Object> claims = new LinkedHashMap<>();
                claims.put("=== ID Token Claims ===", "");
                claims.putAll(oidcUser.getClaims());
                if (oidcUser.getIdToken() != null) {
                    claims.put("=== Access Token (raw) ===", oidcUser.getIdToken().getTokenValue());
                }
                model.addAttribute("oidcClaims", claims);
            }
        }
    }
}

