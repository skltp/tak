package se.skltp.tak.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.time.Duration;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import se.skltp.tak.web.util.Sha1PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String LOGOUT_PAGE = "/auth/logout";

    @Value("${spring.sql.init.platform}")
    String dbPlatform;

    @Value("${spring.security.oauth2.client.provider.keycloak.end-session-uri}")
    private String keycloakEndSessionUri;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String oauth2ClientId;

    @Value("${keycloak.post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    @Bean
    @Profile("forwardauth")
    @Order(1)
    SecurityFilterChain forwardAuthChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/forwardauth/**")
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(reg -> reg.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/forwardauth/**"));
        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   @Value("${tak.web.csrf.active:true}") boolean useCsrf,
                                                   SessionUserValidationFilter validationFilter) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/auth/login",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/favicon.ico",
                                "/error",
                                "/static/**",
                                "/actuator/**"
                        ).permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(request -> request.getServletPath().equals(LOGOUT_PAGE))
                        .logoutSuccessHandler(keycloakLogoutSuccessHandler())
                        .permitAll()
                )
                .addFilterAfter(validationFilter, UsernamePasswordAuthenticationFilter.class);

        if (!useCsrf) {
            http.csrf(AbstractHttpConfigurer::disable);
        }

        if (dbPlatform != null && dbPlatform.equals("h2")) {
            http.csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()).disable());
            http.authorizeHttpRequests(req -> req.requestMatchers(toH2Console()).permitAll());
        }

        http.authorizeHttpRequests(req -> req.anyRequest().authenticated());

        return http.build();
    }

    private LogoutSuccessHandler keycloakLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            StringBuilder url = new StringBuilder(keycloakEndSessionUri)
                    .append("?client_id=").append(oauth2ClientId)
                    .append("&post_logout_redirect_uri=").append(postLogoutRedirectUri);

            if (authentication instanceof OAuth2AuthenticationToken token &&
                token.getPrincipal() instanceof OidcUser oidcUser) {
                url.append("&id_token_hint=").append(oidcUser.getIdToken().getTokenValue());
            }

            response.sendRedirect(url.toString());
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Sha1PasswordEncoder();
    }

    @Bean
    public CookieSerializer cookieSerializer(@Value("${server.servlet.session.cookie.name}") String sessionCookieName,
                                             @Value("${server.servlet.session.cookie.timeout:12h}") Duration maxAge) {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(sessionCookieName);
        serializer.setCookieMaxAge((int) maxAge.getSeconds());
        return serializer;
    }

}
