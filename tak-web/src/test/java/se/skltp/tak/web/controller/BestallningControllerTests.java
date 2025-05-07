package se.skltp.tak.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.skltp.tak.web.configuration.TestSecurityConfig;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.service.AnvandareService;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.BestallningsStodetConnectionService;
import se.skltp.tak.web.service.ConfigurationService;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BestallningController.class)
@Import(TestSecurityConfig.class)
class BestallningControllerTests {

    private static final String TEST_USER = "TEST_USER";
    @Autowired private MockMvc mockMvc;

    @MockBean private AnvandareService anvandareService;
    @MockBean private BestallningService bestallningService;
    @MockBean private BestallningsStodetConnectionService bestallningsStodetConnectionService;
    @MockBean(name = "configurationService") private ConfigurationService configurationService;
    private static final String BESTALLNINGS_NUMMER = "bestallningsNummer";
    private static final String BESTALLNING_JSON = "bestallningJson";

    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningStartPageTest() throws Exception {
        when(bestallningsStodetConnectionService.isActive()).thenReturn(true);
        mockMvc.perform(get("/bestallning")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Skapa Beställning")))
                .andExpect(content().string(containsString("Hämta")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningConnectionOffTest() throws Exception {
        when(bestallningsStodetConnectionService.isActive()).thenReturn(false);
        mockMvc.perform(get("/bestallning")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Hämta"))))
                .andExpect(content().string(containsString("Hämtning av beställning via beställningsnummer är avstängt")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningConfigErrorsTest() throws Exception {
        when(bestallningsStodetConnectionService.isActive()).thenReturn(true);
        Set<String> configErrors = new HashSet<>();
        configErrors.add("ERROR 1");
        configErrors.add("ERROR 2");
        when(bestallningsStodetConnectionService.checkBestallningConfiguration()).thenReturn(configErrors);
        mockMvc.perform(get("/bestallning")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Hämta"))))
                .andExpect(content().string(containsString("ERROR 1")))
                .andExpect(content().string(containsString("ERROR 2")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningGetTest() throws Exception {
        when(bestallningsStodetConnectionService.getBestallning(42L, 0)).thenReturn("{}");
        when(bestallningService.parseAndFormatJson("{}")).thenReturn("{ \"formatted\" : \"true\" }");
        mockMvc.perform(post("/bestallning")
                        .param(BESTALLNINGS_NUMMER, "42"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute(BESTALLNINGS_NUMMER, 42L))
                .andExpect(model().attribute(BESTALLNING_JSON, "{ \"formatted\" : \"true\" }"))
                .andExpect(content().string(containsString("Skapa Beställning")));
        verify(bestallningsStodetConnectionService, times(1)).getBestallning(42, 0);
    }

    @Test
    @WithMockUser(username = TEST_USER)
    public void bestallningGetErrorTest() throws Exception {
        when(bestallningsStodetConnectionService.getBestallning(99L, 0)).thenThrow(new FileNotFoundException());
        mockMvc.perform(post("/bestallning")
                        .param("bestallningsNummer", "99"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("bestallningsNummer", 99L))
                .andExpect(content().string(containsString("Kunde inte hämta beställning")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    public void bestallningParseErrorTest() throws Exception {
        when(bestallningsStodetConnectionService.getBestallning(98L, 0)).thenReturn("");
        when(bestallningService.parseAndFormatJson("")).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/bestallning")
                        .param("bestallningsNummer", "98"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("bestallningsNummer", 98L))
                .andExpect(content().string(containsString("Beställning 98 kunde inte tolkas")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    public void bestallningConfirmTest() throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        BestallningsRapport mockRapport = mock(BestallningsRapport.class);
        when(mockRapport.getMetadata()).thenReturn(new LinkedHashMap<>());
        when(mockRapport.getInkludera()).thenReturn(new LinkedHashMap<>());
        when(mockRapport.getExkludera()).thenReturn(new LinkedHashMap<>());
        when(mockData.getBestallningsRapport()).thenReturn(mockRapport);
        when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);

        mockMvc.perform(post("/bestallning/confirm")
                        .param(BESTALLNING_JSON, "{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("bestallningHash", mockData.hashCode()))
                .andExpect(MockMvcResultMatchers.request().sessionAttribute("bestallning", "{}"))
                .andExpect(content().string(containsString("Bekräfta Beställning")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    public void bestallningConfirmValidationErrorsTest() throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        when(mockData.hasErrors()).thenReturn(true);
        Set<String> errors = new HashSet<>();
        errors.add("ERROR 1");
        errors.add("ERROR 2");
        when(mockData.getBestallningErrors()).thenReturn(errors);

        when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);

        mockMvc.perform(post("/bestallning/confirm")
                        .param(BESTALLNING_JSON, "{}"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bestallning"));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    public void bestallningConfirmExceptionTest() throws Exception {
        when(bestallningService.buildBestallningsData(anyString(), anyString())).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/bestallning/confirm")
                        .param(BESTALLNING_JSON, "{}"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bestallning"));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    public void bestallningCancelTest() throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("bestallning", mockData);

        mockMvc.perform(get("/bestallning/cancel")
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bestallning"));
        assertNull(mockSession.getAttribute("bestallning"));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    public void bestallningSaveWrongHashTest() throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("bestallning", mockData);

        when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);

        mockMvc.perform(post("/bestallning/save")
                        .param("bestallningHash", "13")
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ett fel inträffade")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    public void bestallningSaveTest() throws Exception {
        BestallningsData mockData = Mockito.mock(BestallningsData.class);
        BestallningsRapport mockRapport = Mockito.mock(BestallningsRapport.class);
        when(mockRapport.toString()).thenReturn("The TEST report text.");
        when(mockData.getBestallningsRapport()).thenReturn(mockRapport);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("bestallning", mockData);

        when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);
        mockMvc.perform(post("/bestallning/save")
                        .param("bestallningHash", Integer.toString(mockData.hashCode()))
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Denna beställning är sparad")))
                .andExpect(content().string(containsString("The TEST report text.")));
    }
}