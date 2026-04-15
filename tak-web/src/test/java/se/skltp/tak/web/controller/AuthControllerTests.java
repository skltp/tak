package se.skltp.tak.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.configuration.SecurityConfig;
import se.skltp.tak.web.service.ConfigurationService;
import se.skltp.tak.web.util.Sha1PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Import({SecurityConfig.class, Sha1PasswordEncoder.class, ConfigurationService.class, BuildProperties.class})
class AuthControllerTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void cleanSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testPasswordHashingCompatibility() {
        String rawPassword = "skltp";
        String hashedPassword = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, hashedPassword));
    }

    @Test
    void testLoginPageIsAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    void testStaticResourcesAreAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/static/css/main1.css"))
                .andExpect(status().isOk());
    }

}


