package se.skltp.tak.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.service.ConfigurationService;
import se.skltp.tak.web.service.ConnectionsService;

@WebMvcTest(HomeController.class)
@ExtendWith(MockitoExtension.class)
public class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "connectionsService")
    private ConnectionsService connectionsService;

    @Mock
    private Authentication authentication;

    @MockBean(name = "configurationService")
    private ConfigurationService configurationService;

    @MockBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setup() {
        // Mock SecurityContextHolder
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(User.withUsername("testuser").password("pass").roles("USER").build());

        when(connectionsService.isAvailable()).thenReturn(false);


    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void homePageSmokeTest() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Administrera RIV-TA-profiler")));
    }
}
