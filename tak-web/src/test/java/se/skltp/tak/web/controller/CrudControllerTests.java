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
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.service.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CrudController.class)
public class CrudControllerTests {

    @Autowired private MockMvc mockMvc;

    @MockBean AnvandareService anvandareServiceMock;
    @MockBean(name = "configurationService") ConfigurationService configurationService;
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

    @Test
    public void createFilterSetsAnropsbehorighetAndServiceDomainTest () throws Exception {
        when(filterService.getEntityName()).thenReturn("Filter");
        when(anropsBehorighetService.getAnropsbehorighet(3L, 2L, 1L))
                .thenReturn(new Anropsbehorighet());
        when(filterService.add(any(), any())).thenReturn(new Filter());
        when(filterService.add(any(Filter.class), any(String.class))).thenReturn(new Filter());

        mockMvc.perform(post("/filter/create")
                        .param("tjanstekontrakt", "1")
                        .param("tjanstekonsument", "2")
                        .param("logiskAdress", "3")
                        .param("servicedomain", "urn:x.y.z")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/filter"))
                .andExpect(flash().attribute("message", "Filter skapad"));

        verify(filterService, times(1))
                .add(argThat(a -> a.getAnropsbehorighet() != null && a.getServicedomain() != null), any(String.class));
    }

    @Test
    public void createFilterIllegalIdTest () throws Exception {
        when(filterService.getEntityName()).thenReturn("Filter");
        when(anropsBehorighetService.getAnropsbehorighet(3L, 2L, 1L))
                .thenReturn(new Anropsbehorighet());
        when(filterService.add(any(), any())).thenReturn(new Filter());
        when(filterService.add(any(Filter.class), any(String.class))).thenReturn(new Filter());

        mockMvc.perform(post("/filter/create")
                        .param("tjanstekontrakt", "-1")
                        .param("tjanstekonsument", "2")
                        .param("logiskAdress", "3")
                        .param("servicedomain", "urn:x.y.z")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void updateFilterSetsAnropsbehorighetAndServiceDomainTest() throws Exception {
        when(filterService.getEntityName()).thenReturn("Filter");
        Anropsbehorighet mockAb = new Anropsbehorighet();
        mockAb.setId(42L);
        when(anropsBehorighetService.getAnropsbehorighet(5L, 2L, 14L))
                .thenReturn(mockAb);
        when(filterService.update(any(), any())).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(post("/filter/update")
                        .param("id", "4")
                        .param("version", "1")
                        .param("tjanstekontrakt", "14")
                        .param("tjanstekonsument", "2")
                        .param("logiskAdress", "5")
                        .param("servicedomain", "urn:a.b.c")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/filter"))
                .andExpect(flash().attribute("message", "Filter uppdaterad"));

        verify(filterService, times(1))
                .update(argThat(a -> a.getAnropsbehorighet().getId() == 42 && a.getServicedomain() == "urn:a.b.c"), any(String.class));
    }

    @Test
    public void updateFilterNoAnropsbehorighetTest () throws Exception {
        when(filterService.getEntityName()).thenReturn("Filter");
        when(anropsBehorighetService.getAnropsbehorighet(anyLong(),anyLong(),anyLong())).thenReturn(null);

        mockMvc.perform(post("/filter/update")
                        .param("id", "4")
                        .param("version", "1")
                        .param("tjanstekontrakt", "14")
                        .param("tjanstekonsument", "2")
                        .param("logiskAdress", "5")
                        .param("servicedomain", "urn:a.b.c")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }
}
