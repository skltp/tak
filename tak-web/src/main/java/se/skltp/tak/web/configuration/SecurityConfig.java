package se.skltp.tak.web.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import se.skltp.tak.web.util.Sha1PasswordEncoder;

import java.io.IOException;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String LOGIN_PAGE = "/auth/login";
    private static final String LOGOUT_PAGE = "/auth/logout";
    @Value("${spring.sql.init.platform}")
    String dbPlatform;




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Value("${tak.web.csrf.active:true}") boolean useCsrf) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/auth/**",
                                "/favicon.ico",
                                "/error",
                                "/static/**",
                                "/actuator/**"
                        ).permitAll()
                )
                .formLogin(form -> form
                        .loginPage(LOGIN_PAGE)
                        .loginProcessingUrl(LOGIN_PAGE)
                        .defaultSuccessUrl("/", false)
                        .failureUrl(LOGIN_PAGE + "?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(LOGOUT_PAGE)
                        .logoutSuccessUrl(LOGIN_PAGE)
                        .permitAll()
                )
                .requestCache(cache -> cache.requestCache(requestCache));
        if (!useCsrf) {
            http.csrf(AbstractHttpConfigurer::disable);
        } else {
            http.exceptionHandling(exceptions ->
                    exceptions.accessDeniedHandler(new AccessDeniedHandler() {
                        @Override
                        public void handle(HttpServletRequest request,
                                           HttpServletResponse response,
                                           AccessDeniedException accessDeniedException)
                                throws IOException {
                            if (accessDeniedException instanceof MissingCsrfTokenException ||
                                accessDeniedException instanceof InvalidCsrfTokenException) {
                                response.sendRedirect(request.getContextPath() + LOGIN_PAGE + "?csrfError=true");
                            } else {
                                response.sendRedirect(request.getContextPath() + LOGIN_PAGE + "?error=true");
                            }
                        }
                    }
                    )
            );
        }

        if (dbPlatform != null && dbPlatform.equals("h2")) {
            http.csrf(csrf -> csrf
                    .ignoringRequestMatchers(toH2Console())
                    .disable());
            http.authorizeHttpRequests(req -> req.requestMatchers(toH2Console()).permitAll());
        }

        http.authorizeHttpRequests( req -> req.anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
          return new Sha1PasswordEncoder();  // Använd SHA-1 för att matcha Shiro's hashning
    }

    @Bean
    public CookieSerializer cookieSerializer(@Value("${spring.session.cookie-name:JSESSION}") String sessionCookieName) {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(sessionCookieName);
        return serializer;
    }

}
