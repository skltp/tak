package se.skltp.tak.web.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import se.skltp.tak.web.util.Sha1PasswordEncoder;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.sql.init.platform}")
    String dbPlatform;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/signIn")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login")
                        .permitAll()
                )
                .requestCache(cache -> cache.requestCache(requestCache));

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
}
