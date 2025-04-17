package se.skltp.tak.web.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SessionUserValidationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    public SessionUserValidationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();

            try {
                userDetailsService.loadUserByUsername(username); // kontroll mot databasen
            } catch (UsernameNotFoundException e) {
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
                response.sendRedirect(request.getContextPath());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
