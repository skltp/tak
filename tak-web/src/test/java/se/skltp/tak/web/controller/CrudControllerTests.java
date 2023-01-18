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
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.service.*;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void showSetsInstanceToModelTest () throws Exception {
        when(logiskAdressService.getEntityName()).thenReturn("Logisk adress");
        LogiskAdress mockLA = new LogiskAdress();
        mockLA.setId(42L);
        mockLA.setHsaId("ADRESS-42");
        when(logiskAdressService.findById(eq(42L))).thenReturn(Optional.of(mockLA));

        mockMvc.perform(get("/logiskAdress/42")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("instance", mockLA));
    }

    @Test
    public void showRedirectsToListOnWrongIdTest () throws Exception {
        when(logiskAdressService.getEntityName()).thenReturn("Logisk adress");
        when(logiskAdressService.findById(eq(313L))).thenReturn(Optional.empty());

        mockMvc.perform(get("/logiskAdress/313")).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logiskAdress"))
                .andExpect(flash().attribute("errors", "Logisk adress med id 313 hittades ej."));
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
    public void objectOptimisticLockingFailureExceptionMessageTest () throws Exception {
        when(anropsAdressService.getEntityName()).thenReturn("Anropsadress");
        when(anropsAdressService.update(any(AnropsAdress.class), any(String.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException("message", new org.hibernate.StaleObjectStateException("Anropsadress", 55)));

        mockMvc.perform(post("/anropsadress/update")
                        .param("id", "55")
                        .param("version", "3")
                        .param("adress", "https://example.com/soap")
                        .param("tjanstekomponent.id", "11")
                        .param("rivTaProfil.id", "3")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anropsadress"))
                .andExpect(flash().attributeExists("errors"));
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
