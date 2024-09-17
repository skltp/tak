package se.skltp.tak.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.configuration.TestSecurityConfig;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.service.AnvandareService;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.ConfigurationService;

import java.util.HashSet;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BestallningRestController.class)
@Import(TestSecurityConfig.class)
class BestallningRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private AnvandareService anvandareService;
    @MockBean private BestallningService bestallningService;
    @MockBean(name = "configurationService") private ConfigurationService configurationService;

    @Test
    @WithMockUser(username = "TEST_USER")
    void restBestallningTest() throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        BestallningsRapport mockRapport = Mockito.mock(BestallningsRapport.class);
        Mockito.when(mockData.hasErrors()).thenReturn(false);
        Mockito.when(mockData.getBestallningsRapport()).thenReturn(mockRapport);
        Mockito.when(mockRapport.toString()).thenReturn("Mockad beställningsrapport");
        Mockito.when(bestallningService.parseAndFormatJson("{ hej }")).thenReturn("{ \"formatted\" : \"true\" }");
        Mockito.when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);
        mockMvc.perform(post("/rest/create").content("{ hej }").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mockad beställningsrapport")));
    }

    @Test
    @WithMockUser(username = "TEST_USER")
    void restBestallningErrorTest() throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        Mockito.when(mockData.hasErrors()).thenReturn(true);
        HashSet<String> errors = new HashSet<>();
        errors.add("Error 1");
        errors.add("Error 2");
        Mockito.when(mockData.getBestallningErrors()).thenReturn(errors);
        Mockito.when(bestallningService.parseAndFormatJson("{ hej }")).thenReturn("{ \"formatted\" : \"true\" }");
        Mockito.when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);
        mockMvc.perform(post("/rest/create").content("{ hej }").contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Error 1")))
                .andExpect(content().string(containsString("Error 2")));
    }
}
