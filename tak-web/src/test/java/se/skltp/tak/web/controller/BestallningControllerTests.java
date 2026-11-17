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
import se.skltp.tak.web.service.MasstakningService;
import se.skltp.tak.web.exception.MasstakningException;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BestallningController.class, properties = "server.tomcat.max-http-form-post-size=5MB")
@Import(TestSecurityConfig.class)
class BestallningControllerTests {

    static final String TEST_USER = "TEST_USER";
    @Autowired MockMvc mockMvc;

    @MockitoBean AnvandareService anvandareService;
    @MockitoBean BestallningService bestallningService;
    @MockitoBean BestallningsStodetConnectionService bestallningsStodetConnectionService;
    @MockitoBean(name = "configurationService") ConfigurationService configurationService;
    @MockitoBean MasstakningService masstakningService;
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
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void bestallningStartPageShowsMaxSizeTest() throws Exception {
        mockMvc.perform(get("/bestallning"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-max-size=\"5242880\"")))
                .andExpect(content().string(containsString("beställningen får vara högst 5,0 MB när den skickas")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningStartPageHidesMaxSizeInfoForNonAdminTest() throws Exception {
        mockMvc.perform(get("/bestallning"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("data-max-size=\"5242880\"")))
                .andExpect(content().string(not(containsString("beställningen får vara högst"))));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void masstakningHiddenForNonAdminTest() throws Exception {
        mockMvc.perform(get("/bestallning"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("masstakning-form"))));
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void masstakningShownForAdminTest() throws Exception {
        mockMvc.perform(get("/bestallning"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("MassTAKning från excel")))
                .andExpect(content().string(containsString("<option value=\"NTJP-PROD\" selected=\"selected\">")))
                .andExpect(content().string(containsString("<option value=\"TAKA\" selected=\"selected\">")));
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void masstakningFillsBestallningTest() throws Exception {
        LinkedHashMap<String, Object> takOrder = new LinkedHashMap<>();
        takOrder.put("plattform", "NTJP-QA");
        when(masstakningService.createTakOrder(any(), eq("NTJP-QA"), eq("AV-TAKA"))).thenReturn(takOrder);
        MockMultipartFile file = new MockMultipartFile("file", "region.xlsx", "application/octet-stream", new byte[]{1});

        mockMvc.perform(multipart("/bestallning/masstakning").file(file).param("plattform", "NTJP-QA").param("action", "AV-TAKA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&quot;plattform&quot;: &quot;NTJP-QA&quot;")))
                .andExpect(content().string(containsString("Beställning skapad från region.xlsx (NTJP-QA, AV-TAKA)")))
                .andExpect(content().string(containsString("<option value=\"AV-TAKA\" selected=\"selected\">")));
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void masstakningErrorShownAndKeepsSelectionsTest() throws Exception {
        when(masstakningService.createTakOrder(any(), any(), any())).thenThrow(new MasstakningException("Fel i excel"));
        MockMultipartFile file = new MockMultipartFile("file", "fel.xlsx", "application/octet-stream", new byte[]{1});

        mockMvc.perform(multipart("/bestallning/masstakning").file(file).param("plattform", "SLL-PROD").param("action", "AV-TAKA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Fel i excel")))
                .andExpect(content().string(containsString("<option value=\"SLL-PROD\" selected=\"selected\">")))
                .andExpect(content().string(containsString("<option value=\"AV-TAKA\" selected=\"selected\">")));
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void masstakningMissingFileShowsErrorTest() throws Exception {
        mockMvc.perform(multipart("/bestallning/masstakning").param("plattform", "NTJP-PROD").param("action", "TAKA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ingen excel-fil har angetts.")));
        verifyNoInteractions(masstakningService);
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void masstakningRequiresAdminTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "region.xlsx", "application/octet-stream", new byte[]{1});
        mockMvc.perform(multipart("/bestallning/masstakning").file(file))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(masstakningService);
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningTooLargeShowsErrorTest() throws Exception {
        mockMvc.perform(get("/bestallning").param("tooLarge", "6500000"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Beställningen är för stor (6,2 MB). Maximal storlek är 5,0 MB.")));
    }

    @Test
    @WithMockUser(username = TEST_USER)
    void bestallningConfirmWithoutJsonRedirectsWithErrorTest() throws Exception {
        mockMvc.perform(post("/bestallning/confirm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bestallning"))
                .andExpect(flash().attribute("errors", java.util.List.of(
                        "Beställningen saknas eller är för stor. Maximal storlek är 5,0 MB.")));
        verifyNoInteractions(bestallningService);
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