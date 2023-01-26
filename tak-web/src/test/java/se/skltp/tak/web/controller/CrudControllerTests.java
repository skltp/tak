package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.service.*;
import se.skltp.tak.web.validator.EntityValidator;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Test the CrudController with custom input validation but everything else mocked
@WebMvcTest(controllers = CrudController.class,
        includeFilters = @ComponentScan.Filter(value = EntityValidator.class, type = FilterType.ASSIGNABLE_TYPE))
public class CrudControllerTests {

    @Autowired private MockMvc mockMvc;

    @MockBean AnvandareService anvandareServiceMock;
    @MockBean(name = "configurationService") ConfigurationService configurationServiceMock;
    @MockBean RivTaProfilService rivTaProfilServiceMock;
    @MockBean TjanstekontraktService tjanstekontraktServiceMock;
    @MockBean TjanstekomponentService tjanstekomponentServiceMock;
    @MockBean LogiskAdressService logiskAdressServiceMock;
    @MockBean VagvalService vagvalServiceMock;
    @MockBean AnropsAdressService anropsAdressServiceMock;
    @MockBean AnropsBehorighetService anropsBehorighetServiceMock;
    @MockBean FilterService filterServiceMock;
    @MockBean FilterCategorizationService filterCategorizationServiceMock;

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
        when(logiskAdressServiceMock.getEntityName()).thenReturn("Logisk adress");
        LogiskAdress mockLA = new LogiskAdress();
        mockLA.setId(42L);
        mockLA.setHsaId("ADRESS-42");
        when(logiskAdressServiceMock.findById(eq(42L))).thenReturn(Optional.of(mockLA));

        mockMvc.perform(get("/logiskAdress/42")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("instance", mockLA));
    }

    @Test
    public void showRedirectsToListOnWrongIdTest () throws Exception {
        when(logiskAdressServiceMock.getEntityName()).thenReturn("Logisk adress");
        when(logiskAdressServiceMock.findById(eq(313L))).thenReturn(Optional.empty());

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
        verify(rivTaProfilServiceMock).add(any(RivTaProfil.class), eq("TEST_USER"));
    }

    @Test
    public void createSetsFlashAttributeTest () throws Exception {
        when(tjanstekomponentServiceMock.getEntityName()).thenReturn("Tjänstekomponent");

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
        when(tjanstekomponentServiceMock.getEntityName()).thenReturn("Tjänstekomponent");

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

    // Entity input validation tests

    @Test
    public void validateCorrectAnropsAdressTest () throws Exception {
        when(anropsAdressServiceMock.getEntityName()).thenReturn("Anropsadress");
        mockMvc.perform(post("/anropsadress/create")
                        .param("adress", "https://example.com/soap-service")
                        .param("tjanstekomponent.id", "2")
                        .param("rivTaProfil.id", "3")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anropsadress"))
                .andExpect(flash().attribute("message", "Anropsadress skapad"));
    }

    @Test
    public void validateDuplicateConstraintTest () throws Exception {
        when(anropsAdressServiceMock.getEntityName()).thenReturn("Anropsadress");
        when(anropsAdressServiceMock.hasDuplicate(any(AnropsAdress.class))).thenReturn(true);
        mockMvc.perform(post("/anropsadress/create")
                        .param("adress", "https://example.com/soap")
                        .param("tjanstekomponent.id", "2")
                        .param("rivTaProfil.id", "3")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void checkForMappingErrorsTest () throws Exception {
        when(anropsAdressServiceMock.getEntityName()).thenReturn("Anropsadress");
        mockMvc.perform(post("/anropsadress/create")
                        .param("adress", "https://example.com/soap-service")
                        .param("tjanstekomponent.id", "fel")
                        .param("rivTaProfil.id", "3")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void validateAnropsaAdressFormatTest () throws Exception {
        when(anropsAdressServiceMock.getEntityName()).thenReturn("Anropsadress");
        mockMvc.perform(post("/anropsadress/create")
                        .param("adress", "https://söap.example.com")
                        .param("tjanstekomponent.id", "4")
                        .param("rivTaProfil.id", "3")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1));
    }

    @Test
    public void validateLengthTest () throws Exception {
        when(anropsAdressServiceMock.getEntityName()).thenReturn("Anropsadress");
        mockMvc.perform(post("/anropsadress/update")
                        .param("id", "55")
                        .param("version", "3")
                        .param("adress", "http")
                        .param("tjanstekomponent.id", "4")
                        .param("rivTaProfil.id", "3")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1));
    }

    @Test
    public void objectOptimisticLockingFailureExceptionMessageTest () throws Exception {
        when(tjanstekomponentServiceMock.getEntityName()).thenReturn("Tjänstekomponent");
        when(tjanstekomponentServiceMock.update(any(Tjanstekomponent.class), any(String.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException("message", new org.hibernate.StaleObjectStateException("Anropsadress", 55)));

        mockMvc.perform(post("/tjanstekomponent/update")
                        .param("id", "42")
                        .param("version", "3")
                        .param("hsaId", "TEST_HSA_ID")
                        .param("beskrivning", "TEST_NY_BESKRIVNING")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tjanstekomponent"))
                .andExpect(flash().attributeExists("errors"));
    }

    @Test
    public void validateLeadingSpaceTest () throws Exception {
        when(anropsBehorighetServiceMock.getEntityName()).thenReturn("Anropsbehörighet");
        mockMvc.perform(post("/anropsbehorighet/create")
                        .param("integrationsavtal", " Avtal")
                        .param("tjanstekonsument.id", "4")
                        .param("tjanstekontrakt.id", "5")
                        .param("logiskAdress.id", "3")
                        .param("fromTidpunkt", "2022-01-01")
                        .param("tomTidpunkt", "2023-01-01")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1));
    }

    @Test
    public void validateDateOrderTest () throws Exception {
        when(anropsBehorighetServiceMock.getEntityName()).thenReturn("Anropsbehörighet");
        mockMvc.perform(post("/anropsbehorighet/create")
                        .param("integrationsavtal", "Avtal")
                        .param("tjanstekonsument.id", "4")
                        .param("tjanstekontrakt.id", "5")
                        .param("logiskAdress.id", "3")
                        .param("fromTidpunkt", "2023-05-11")
                        .param("tomTidpunkt", "2023-01-01")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1));
    }

    // Filter specific tests

    @Test
    public void createFilterSetsAnropsbehorighetAndServiceDomainTest () throws Exception {
        when(filterServiceMock.getEntityName()).thenReturn("Filter");
        when(anropsBehorighetServiceMock.getAnropsbehorighet(3L, 2L, 1L))
                .thenReturn(new Anropsbehorighet());
        when(filterServiceMock.add(any(), any())).thenReturn(new Filter());
        when(filterServiceMock.add(any(Filter.class), any(String.class))).thenReturn(new Filter());

        mockMvc.perform(post("/filter/create")
                        .param("tjanstekontrakt", "1")
                        .param("tjanstekonsument", "2")
                        .param("logiskAdress", "3")
                        .param("servicedomain", "urn:x.y.z")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/filter"))
                .andExpect(flash().attribute("message", "Filter skapad"));

        verify(filterServiceMock, times(1))
                .add(argThat(a -> a.getAnropsbehorighet() != null && a.getServicedomain() != null), any(String.class));
    }

    @Test
    public void createFilterIllegalIdTest () throws Exception {
        when(filterServiceMock.getEntityName()).thenReturn("Filter");
        when(anropsBehorighetServiceMock.getAnropsbehorighet(3L, 2L, 1L))
                .thenReturn(new Anropsbehorighet());
        when(filterServiceMock.add(any(), any())).thenReturn(new Filter());
        when(filterServiceMock.add(any(Filter.class), any(String.class))).thenReturn(new Filter());

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
    public void createFilterDuplicateTest () throws Exception {
        when(filterServiceMock.getEntityName()).thenReturn("Filter");
        when(anropsBehorighetServiceMock.getAnropsbehorighet(3L, 2L, 1L))
                .thenReturn(new Anropsbehorighet());
        when(filterServiceMock.hasDuplicate(any(Filter.class))).thenReturn(true);
        when(filterServiceMock.add(any(), any())).thenReturn(new Filter());
        when(filterServiceMock.add(any(Filter.class), any(String.class))).thenReturn(new Filter());

        mockMvc.perform(post("/filter/create")
                        .param("tjanstekontrakt", "1")
                        .param("tjanstekonsument", "2")
                        .param("logiskAdress", "3")
                        .param("servicedomain", "urn:x.y.z")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());
    }

    @Test
    public void updateFilterSetsAnropsbehorighetAndServiceDomainTest() throws Exception {
        when(filterServiceMock.getEntityName()).thenReturn("Filter");
        Anropsbehorighet mockAb = new Anropsbehorighet();
        mockAb.setId(42L);
        when(anropsBehorighetServiceMock.getAnropsbehorighet(5L, 2L, 14L))
                .thenReturn(mockAb);
        when(filterServiceMock.update(any(), any())).thenAnswer(i -> i.getArguments()[0]);

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

        verify(filterServiceMock, times(1))
                .update(argThat(a -> a.getAnropsbehorighet().getId() == 42 && a.getServicedomain() == "urn:a.b.c"), any(String.class));
    }

    @Test
    public void updateFilterNoAnropsbehorighetTest () throws Exception {
        when(filterServiceMock.getEntityName()).thenReturn("Filter");
        when(anropsBehorighetServiceMock.getAnropsbehorighet(anyLong(),anyLong(),anyLong())).thenReturn(null);

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
