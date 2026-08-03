/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.skltp.tak.web.configuration.TestSecurityConfig;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.service.AnvandareService;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.BestallningsStodetConnectionService;
import se.skltp.tak.web.service.ConfigurationService;

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

    static final String TEST_USER = "TEST_USER";
    @Autowired MockMvc mockMvc;

    @MockitoBean AnvandareService anvandareService;
    @MockitoBean BestallningService bestallningService;
    @MockitoBean BestallningsStodetConnectionService bestallningsStodetConnectionService;
    @MockitoBean(name = "configurationService") ConfigurationService configurationService;
    static final String BESTALLNING_JSON = "bestallningJson";

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
    void bestallningConfirmTest() throws Exception {
        BestallningsData mockData = mock(BestallningsData.class);
        BestallningsRapport mockRapport = mock(BestallningsRapport.class);
        when(mockRapport.getRapportHuvud()).thenReturn(new LinkedHashMap<>());
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
    void bestallningConfirmValidationErrorsTest() throws Exception {
        BestallningsData mockData = mock(BestallningsData.class);
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
    void bestallningConfirmExceptionTest() throws Exception {
        when(bestallningService.buildBestallningsData(anyString(), anyString())).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/bestallning/confirm")
                        .param(BESTALLNING_JSON, "{}"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bestallning"));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningCancelTest() throws Exception {
        BestallningsData mockData = mock(BestallningsData.class);
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
    void bestallningSaveWrongHashTest() throws Exception {
        BestallningsData mockData = mock(BestallningsData.class);
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
    void bestallningSaveTest() throws Exception {
        BestallningsData mockData = mock(BestallningsData.class);
        BestallningsRapport mockRapport = mock(BestallningsRapport.class);
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

    /**
     * NTU-359: Att skicka om POST /bestallning/save (t.ex. via webbläsarens bakåtknapp efter att
     * beställningen redan sparats) ska inte ge ett ohanterat fel (NullPointerException) utan
     * en informativ sida.
     */
    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningSaveWithoutSessionDataTest() throws Exception {
        MockHttpSession mockSession = new MockHttpSession();

        mockMvc.perform(post("/bestallning/save")
                        .param("bestallningHash", "13")
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("saved", false))
                .andExpect(content().string(containsString("Ett fel inträffade")))
                .andExpect(content().string(containsString("Beställningen kunde inte sparas")));

        assertNull(mockSession.getAttribute("bestallning"));
        verify(bestallningService, never()).execute(any(), anyString());
    }

    /**
     * NTU-359: Andra anropet (omsändning) ska varken krascha eller exekvera beställningen igen.
     */
    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningSaveTwiceTest() throws Exception {
        BestallningsData mockData = mock(BestallningsData.class);
        BestallningsRapport mockRapport = mock(BestallningsRapport.class);
        when(mockRapport.toString()).thenReturn("The TEST report text.");
        when(mockData.getBestallningsRapport()).thenReturn(mockRapport);
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("bestallning", mockData);

        when(bestallningService.buildBestallningsData(anyString(), anyString())).thenReturn(mockData);
        String hash = Integer.toString(mockData.hashCode());

        mockMvc.perform(post("/bestallning/save")
                        .param("bestallningHash", hash)
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(model().attribute("saved", true));

        mockMvc.perform(post("/bestallning/save")
                        .param("bestallningHash", hash)
                        .session(mockSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("saved", false))
                .andExpect(content().string(containsString("Beställningen kunde inte sparas")));

        verify(bestallningService, times(1)).execute(any(), anyString());
    }
}