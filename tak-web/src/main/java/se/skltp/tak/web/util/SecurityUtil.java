package se.skltp.tak.web.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


public class SecurityUtil {

    private SecurityUtil(){}

    public static String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        if (authentication != null && authentication.getPrincipal() != null) {
            if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
                String preferred = oidcUser.getPreferredUsername();
                return preferred != null ? preferred : oidcUser.getName();
            } else if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
                return oauth2User.getName();
            } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails details) {
                return details.getUsername();
            } else {
                return authentication.getPrincipal().toString();
            }
        }
        return null;
    }

    public static void checkAdministratorRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied - Admin role required");
        }
    }
}
