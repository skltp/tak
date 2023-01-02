package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
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
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.service.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CrudController.class)
public class CrudControllerTests {

    @Autowired private MockMvc mockMvc;

    @MockBean AnvandareService anvandareServiceMock;
    @MockBean ConfigurationService configurationService;
    @MockBean RivTaProfilService rivTaProfilService;
    @MockBean TjanstekontraktService tjanstekontraktService;
    @MockBean TjanstekomponentService tjanstekomponentService;

    @MockBean LogiskAdressService logiskAdressService;
    @MockBean VagvalService vagvalService;
    @MockBean AnropsAdressService anropsAdressService;
    @MockBean AnropsBehorighetService anropsBehorighetService;
    @MockBean FilterService filterService;
    @MockBean FilterCategorizationService filterCategorizationService;

    MockedStatic<SecurityUtils> securityUtilsMock;
    Subject mockSubject;

    @BeforeEach
    public void setup() {
        securityUtilsMock = Mockito.mockStatic(SecurityUtils.class);
        mockSubject = Mockito.mock(Subject.class);
        when(mockSubject.getPrincipal()).thenReturn("TEST_USER");
        securityUtilsMock.when(SecurityUtils::getSubject).thenReturn(mockSubject);
    }

    @AfterEach
    public void teardown() {
        securityUtilsMock.close();
    }

    @Test
    public void createStoresUserTest () throws Exception {
        mockMvc.perform(post("/rivTaProfil/create")
                        .param("namn", "TEST_NAMN")
                        .param("beskrivning", "TEST_BESKRIVNING")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rivTaProfil"));
        verify(rivTaProfilService).add(any(RivTaProfil.class), eq("TEST_USER"));
    }

    @Test
    public void createSetsFlashAttributeTest () throws Exception {
        when(tjanstekomponentService.getEntityName()).thenReturn("Tjänstekomponent");

        mockMvc.perform(post("/tjanstekomponent/create")
                        .param("hsaId", "TEST_HSA_ID")
                        .param("beskrivning", "TEST_BESKRIVNING")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tjanstekomponent"))
                .andExpect(flash().attribute("message", "Tjänstekomponent skapad"));
    }

    @Test
    public void updateSetsFlashAttributeTest () throws Exception {
        when(tjanstekomponentService.getEntityName()).thenReturn("Tjänstekomponent");

        mockMvc.perform(post("/tjanstekomponent/update")
                        .param("id", "42")
                        .param("version", "0")
                        .param("hsaId", "TEST_HSA_ID")
                        .param("beskrivning", "TEST_NY_BESKRIVNING")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tjanstekomponent"))
                .andExpect(flash().attribute("message", "Tjänstekomponent uppdaterad"));
    }


}
