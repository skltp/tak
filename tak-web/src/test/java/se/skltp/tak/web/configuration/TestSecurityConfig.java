package se.skltp.tak.web.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {


    public static final String TEST_USER = "TEST_USER";
    public static final String ADMIN_USER = "ADMIN_USER";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String ADMIN_PASSWORD = "ADMIN_PASSWORD";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails normalUser =
                User.withDefaultPasswordEncoder()
                        .username(TEST_USER)
                        .password(TEST_PASSWORD)
                        .roles("USER")
                        .build();
        UserDetails adminUser =
                User.withDefaultPasswordEncoder()
                        .username(ADMIN_USER)
                        .password(ADMIN_PASSWORD)
                        .roles("ADMIN")
                        .build();

        return new InMemoryUserDetailsManager(normalUser, adminUser);
    }
}
