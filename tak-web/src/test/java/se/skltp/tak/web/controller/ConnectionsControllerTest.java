package se.skltp.tak.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.configuration.TestSecurityConfig;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.service.ConfigurationService;
import se.skltp.tak.web.service.ConnectionsService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConnectionsController.class)
@Import({TestSecurityConfig.class})
public class ConnectionsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConnectionsService connectionsService;
    @MockitoBean(name = "configurationService") private ConfigurationService configurationService;

    @Test
    @WithMockUser(username = TestSecurityConfig.TEST_USER)
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/connections").secure(true)).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void testAuthorized() throws Exception {
        List<ConnectionStatus> connectionStatusList = new ArrayList<>();
        PagedEntityList<ConnectionStatus> connectionStatusPagedEntityList = new PagedEntityList<>(connectionStatusList, 42, 0, 10);
        when(connectionsService.getActive(0, 10)).thenReturn(connectionStatusPagedEntityList);
        mockMvc.perform(get("/connections").secure(true)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Anslutningar")));
    }
}
