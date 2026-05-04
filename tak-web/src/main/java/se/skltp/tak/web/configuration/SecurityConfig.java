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
import se.skltp.tak.web.security.dpop.DpopOidcUserService;
import se.skltp.tak.web.security.dpop.DpopTokenResponseClient;

import java.time.Duration;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import se.skltp.tak.web.util.Sha1PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String LOGOUT_PAGE = "/auth/logout";

    @Value("${spring.sql.init.platform}")
    String dbPlatform;

    // Form-login chain (default)
    @Bean
    @Profile("!oauth-dpop")
    @Order(2)
    SecurityFilterChain formLoginFilterChain(HttpSecurity http,
                                             @Value("${tak.web.csrf.active:true}") boolean useCsrf,
                                             SessionUserValidationFilter validationFilter) throws Exception {

        if (!useCsrf) http.csrf(AbstractHttpConfigurer::disable);
        applyH2Rules(http);

        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**", "/static/**", "/actuator/**", "/favicon.ico", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(req -> req.getServletPath().equals(LOGOUT_PAGE))
                        .logoutSuccessUrl("/auth/login")
                        .permitAll()
                )
                .addFilterAfter(validationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    // OAuth 2.0/OIDC DPoP chain
    @Bean
    @Profile("oauth-dpop")
    @Order(2)
    SecurityFilterChain keycloakFilterChain(HttpSecurity http,
                                            @Value("${tak.web.csrf.active:true}") boolean useCsrf,
                                            SessionUserValidationFilter validationFilter,
                                            DpopTokenResponseClient dpopTokenResponseClient,
                                            DpopOidcUserService dpopOidcUserService,
                                            @Value("${spring.security.oauth2.client.provider.keycloak.end-session-uri}") String keycloakEndSessionUri,
                                            @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String oauth2ClientId,
                                            @Value("${keycloak.post-logout-redirect-uri}") String postLogoutRedirectUri) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/auth/login", "/oauth2/**", "/login/oauth2/**",
                                "/favicon.ico", "/error", "/static/**", "/actuator/**"
                        ).permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/", true)
                        .tokenEndpoint(token -> token
                                .accessTokenResponseClient(dpopTokenResponseClient.authorizationCode())
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(dpopOidcUserService)
                        )
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(req -> req.getServletPath().equals(LOGOUT_PAGE))
                        .logoutSuccessHandler(keycloakLogoutHandler(keycloakEndSessionUri, oauth2ClientId, postLogoutRedirectUri))
                        .permitAll()
                )
                .addFilterAfter(validationFilter, UsernamePasswordAuthenticationFilter.class);

        if (!useCsrf) http.csrf(AbstractHttpConfigurer::disable);
        applyH2Rules(http);
        http.authorizeHttpRequests(req -> req.anyRequest().authenticated());

        return http.build();
    }

    // Forward-auth chain
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

    // Shared helpers
    private void applyH2Rules(HttpSecurity http) throws Exception {
        if (dbPlatform != null && dbPlatform.equals("h2")) {
            http.csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()).disable());
            http.authorizeHttpRequests(req -> req.requestMatchers(toH2Console()).permitAll());
        }
    }

    private LogoutSuccessHandler keycloakLogoutHandler(String endSessionUri, String clientId, String postLogoutUri) {
        return (request, response, authentication) -> {
            StringBuilder url = new StringBuilder(endSessionUri)
                    .append("?client_id=").append(clientId)
                    .append("&post_logout_redirect_uri=").append(postLogoutUri);
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
    public CookieSerializer cookieSerializer(@Value("${server.servlet.session.cookie.name}") String name,
                                             @Value("${server.servlet.session.cookie.timeout:12h}") Duration maxAge) {
        DefaultCookieSerializer s = new DefaultCookieSerializer();
        s.setCookieName(name);
        s.setCookieMaxAge((int) maxAge.getSeconds());
        return s;
    }
}