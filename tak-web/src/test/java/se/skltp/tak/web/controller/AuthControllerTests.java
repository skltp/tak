package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.service.AnvandareService;
import se.skltp.tak.web.service.ConfigurationService;

import javax.servlet.http.HttpServletRequest;

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

    @MockBean
    ConfigurationService configurationService;

    MockedStatic<WebUtils> webUtilsMock;
    SavedRequest mockSavedRequest;

    MockedStatic<SecurityUtils> securityUtilsMock;
    Subject mockSubject;

    @BeforeEach
    public void setup() {
        webUtilsMock = Mockito.mockStatic(WebUtils.class);
        mockSavedRequest = Mockito.mock(SavedRequest.class);
        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        mockSubject = Mockito.mock(Subject.class);
        securityUtilsMock.when(SecurityUtils::getSubject).thenReturn(mockSubject);
    }

    @AfterEach
    public void teardown() {
        webUtilsMock.close();
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
    public void signInWithRedirectTest () throws Exception {
        doNothing().when(mockSubject).login(any(AuthenticationToken.class));
        when(mockSavedRequest.getRequestURI()).thenReturn("/rivTaProfil");
        webUtilsMock.when(() -> WebUtils.getSavedRequest(any(HttpServletRequest.class))).thenReturn(mockSavedRequest);

        mockMvc.perform(post("/auth/signIn")
                        .param("username", "TEST_USER")
                        .param("password", "TEST_PASSWORD")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rivTaProfil"));
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
