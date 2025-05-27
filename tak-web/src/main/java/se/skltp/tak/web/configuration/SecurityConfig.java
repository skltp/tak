package se.skltp.tak.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import se.skltp.tak.web.util.Sha1PasswordEncoder;

import java.time.Duration;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String LOGIN_PAGE = "/auth/login";
    private static final String LOGOUT_PAGE = "/auth/logout";
    @Value("${spring.sql.init.platform}")
    String dbPlatform;


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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Value("${tak.web.csrf.active:true}") boolean useCsrf, SessionUserValidationFilter validationFilter) throws Exception {
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
                .requestCache(cache -> cache.requestCache(requestCache))
                .addFilterAfter(validationFilter, UsernamePasswordAuthenticationFilter.class);
        if (useCsrf) {
            http.exceptionHandling(execs -> execs.accessDeniedHandler((request, response, exception) ->
                    response.sendRedirect(request.getContextPath() + LOGIN_PAGE +
                            (exception instanceof CsrfException
                                    ? "?csrfError=true"
                                    : "?error=true")
                    )
            ));
        } else {
            http.csrf(AbstractHttpConfigurer::disable);
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
          return new Sha1PasswordEncoder();  // Use SHA-1 to match Shiro's hashing
    }

    @Bean
    public CookieSerializer cookieSerializer(@Value("${server.servlet.session.cookie.name}")String sessionCookieName,
                                             @Value("${server.servlet.session.cookie.timeout:12h}") Duration maxAge) {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(sessionCookieName);
        serializer.setCookieMaxAge((int) maxAge.getSeconds());
        return serializer;
    }

}
