package se.skltp.tak.web.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;
import se.skltp.tak.web.configuration.TestSecurityConfig;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.mapper.connection.ConnectionStatusExportMapper;
import se.skltp.tak.web.service.ConfigurationService;
import se.skltp.tak.web.service.ConnectionsService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConnectionsController.class)
@Import({TestSecurityConfig.class})
class ConnectionsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConnectionsService connectionsService;
    @MockitoBean(name = "configurationService") private ConfigurationService configurationService;

    @TestConfiguration
    static class Config {
        @Bean
        public ConnectionStatusExportMapper connectionStatusExportMapper() {
            return Mappers.getMapper(ConnectionStatusExportMapper.class);
        }
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.TEST_USER)
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/connections").secure(true)).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.TEST_USER)
    void testUnauthorizedExport() throws Exception {
        mockMvc.perform(get("/connections/export").secure(true)).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void testAuthorized() throws Exception {
        List<ConnectionStatus> connectionStatusList = new ArrayList<>();
        PagedEntityList<ConnectionStatus> connectionStatusPagedEntityList = new PagedEntityList<>(connectionStatusList, 42, 0, 10);
        when(connectionsService.getActive(0, 10)).thenReturn(connectionStatusPagedEntityList);
        when(connectionsService.getEntityName()).thenReturn("Anslutningar");
        mockMvc.perform(get("/connections").secure(true)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Anslutningar")));
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void testAuthorizedExport() throws Exception {
        List<ConnectionStatus> connectionStatusList = List.of(
                new ConnectionStatus("hsaId1", "url1", "aaaBaseUrl1"),
                new ConnectionStatus("hsaId2", "url2", "aaaBaseUrl2"),
                new ConnectionStatus("hsaId3", "url3", "aaaBaseUrl3")
        );
        connectionStatusList.get(0).setAnalysisResult(new AnalysisResultV1().tlsProtocol("tlsProtocol1"));
        connectionStatusList.get(1).setAnalysisResult(new AnalysisResultV1().tlsProtocol("tlsProtocol2"));
        connectionStatusList.get(2).setAnalysisResult(new AnalysisResultV1());
        PagedEntityList<ConnectionStatus> connectionStatusPagedEntityList = new PagedEntityList<>(connectionStatusList, 3, 0, 3);
        when(connectionsService.getActive(0, Integer.MAX_VALUE)).thenReturn(connectionStatusPagedEntityList);
        mockMvc.perform(get("/connections/export").secure(true)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceProducer", Matchers.is("hsaId1")));
    }
}
