package se.skltp.tak.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.configuration.SecurityConfig;
import se.skltp.tak.web.service.ConfigurationService;
import se.skltp.tak.web.util.Sha1PasswordEncoder;
import se.skltp.tak.web.util.TakWebUserDetailsService;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import({SecurityConfig.class, TakWebUserDetailsService.class,
        Sha1PasswordEncoder.class, ConfigurationService.class, BuildProperties.class})
public class AuthControllerTests {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSuccessfulAuthentication() {
        // Given valid credentials
        String username = "skltp";
        String password = "skltp"; // Plain password, should be hashed and matched

        // When performing authentication
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Then the user should be successfully authenticated
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
    }

    @Test
    public void testFailedAuthentication() {
        // Given invalid credentials
        String username = "skltp";
        String password = "wrongPassword";

        // Expecting exception for failed authentication
        assertThrows(AuthenticationException.class, () -> {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        });
    }

    @Test
    public void testPasswordHashingCompatibility() {
        // Fetch a user from the database
        String rawPassword = "skltp"; // Test with actual raw password
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Verify the hash matches
        assertTrue(passwordEncoder.matches(rawPassword, hashedPassword));
    }

    @Test
    public void testLoginPageIsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk()); // Kontrollera att sidan kan nås utan autentisering
    }

    @Test
    public void testStaticResourcesAreAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/static/css/main1.css"))
                .andExpect(status().isOk()); // Kontrollera att statiska resurser är tillgängliga
    }

}
