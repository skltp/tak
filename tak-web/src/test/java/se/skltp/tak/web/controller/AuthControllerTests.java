package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.service.AnvandareService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AnvandareService anvandareServiceMock;

    MockedStatic<SecurityUtils> securityUtilsMock;
    Subject mockSubject;

    @BeforeEach
    public void setup() {
        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        mockSubject = Mockito.mock(Subject.class);
        securityUtilsMock.when(SecurityUtils::getSubject).thenReturn(mockSubject);
    }

    @AfterEach
    public void teardown() {
        securityUtilsMock.close();
    }

    @Test
    public void successfulSignInTest () throws Exception {
        doNothing().when(mockSubject).login(any(AuthenticationToken.class));

        mockMvc.perform(post("/auth/signIn")
                        .param("username", "TEST_USER")
                        .param("password", "TEST_PASSWORD")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void failedSignInTest () throws Exception {
        doThrow(new AuthenticationException()).when(mockSubject).login(any(AuthenticationToken.class));

        mockMvc.perform(post("/auth/signIn")
                        .param("username", "TEST_USER")
                        .param("password", "WRONG_PASSWORD")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}
