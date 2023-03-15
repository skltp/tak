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
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.*;
import se.skltp.tak.web.validator.EntityValidator;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.stringContainsInOrder;
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

        when(anropsAdressServiceMock.getEntityName()).thenReturn("Anropsadress");
        when(anropsBehorighetServiceMock.getEntityName()).thenReturn("Anropsbehörighet");
        when(filterServiceMock.getEntityName()).thenReturn("Filter");
        when(logiskAdressServiceMock.getEntityName()).thenReturn("Logisk adress");
        when(tjanstekomponentServiceMock.getEntityName()).thenReturn("Tjänstekomponent");
        when(tjanstekontraktServiceMock.getEntityName()).thenReturn("Tjänstekontrakt");
        when(vagvalServiceMock.getEntityName()).thenReturn("Vägval");
    }

    @AfterEach
    public void teardown() {
        securityUtilsMock.close();
    }

    @Test
    public void indexListTest () throws Exception {
        ArrayList<Filtercategorization> mockListContents = new ArrayList<>();
        PagedEntityList<Filtercategorization> mockList = new PagedEntityList<>(mockListContents, 0, 0, 10);
        when(filterCategorizationServiceMock.getEntityList(anyInt(), anyInt(), anyList(), anyString(), eq(false)))
                .thenReturn(mockList);

        mockMvc.perform(get("/filterCategorization")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("list", mockList));
    }

    @Test
    public void showSetsInstanceToModelTest () throws Exception {
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
        Tjanstekomponent mockTk = new Tjanstekomponent();
        mockTk.setId(33L);
        when(tjanstekomponentServiceMock.add(any(Tjanstekomponent.class), anyString())).thenReturn(mockTk);
        when(tjanstekomponentServiceMock.getId(any(Tjanstekomponent.class))).thenAnswer(i -> ((Tjanstekomponent) i.getArguments()[0]).getId());
        mockMvc.perform(post("/tjanstekomponent/create")
                        .param("hsaId", "TEST_HSA_ID")
                        .param("beskrivning", "TEST_BESKRIVNING")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tjanstekomponent"))
                .andExpect(flash().attribute("message", "Tjänstekomponent med id 33 skapad"));
    }

    @Test
    public void updateSetsFlashAttributeTest () throws Exception {
        when(tjanstekomponentServiceMock.getId(any(Tjanstekomponent.class))).thenReturn(42L);
        mockMvc.perform(post("/tjanstekomponent/update")
                        .param("id", "42")
                        .param("version", "0")
                        .param("hsaId", "TEST_HSA_ID")
                        .param("beskrivning", "TEST_NY_BESKRIVNING")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tjanstekomponent"))
                .andExpect(flash().attribute("message", "Tjänstekomponent med id 42 uppdaterad"));
    }

    // Entity input validation tests

    @Test
    public void validateCorrectAnropsAdressTest () throws Exception {
        mockMvc.perform(post("/anropsadress/create")
                        .param("adress", "https://example.com/soap-service")
                        .param("tjanstekomponent.id", "2")
                        .param("rivTaProfil.id", "3")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anropsadress"))
                .andExpect(flash().attribute("message", stringContainsInOrder("Anropsadress", "skapad")));
    }

    @Test
    public void validateDuplicateConstraintTest () throws Exception {
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
        when(tjanstekomponentServiceMock.getId(any(Tjanstekomponent.class))).thenAnswer(i -> ((Tjanstekomponent) i.getArguments()[0]).getId());
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
                .andExpect(flash().attribute("errors", stringContainsInOrder("Kunde inte uppdatera", "Objektet har ändrats av en annan användare")));
    }

    @Test
    public void missingIdWithUpdateTest () throws Exception {
        when(anropsAdressServiceMock.getId(any(AnropsAdress.class))).thenReturn(0L);
        mockMvc.perform(post("/anropsadress/update")
                        .param("version", "3")
                        .param("adress", "https://example.com/soap-service")
                        .param("tjanstekomponent.id", "4")
                        .param("rivTaProfil.id", "3")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anropsadress"))
                .andExpect(flash().attributeExists("errors"));
    }

    @Test
    public void validateLeadingSpaceTest () throws Exception {
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

    @Test
    public void validateWildcardLogiskAdressTest () throws Exception {
        mockMvc.perform(post("/logiskAdress/create")
                        .param("hsaId", "*")
                        .param("beskrivning", "Standardvägval")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logiskAdress"))
                .andExpect(flash().attribute("message", stringContainsInOrder("Logisk adress", "skapad")));
    }

    @Test
    public void validateCorrectHsaIdTest () throws Exception {
        mockMvc.perform(post("/tjanstekomponent/create")
                        .param("hsaId", "SE0123456789-ABC_xyz")
                        .param("beskrivning", "Godkända tecken")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tjanstekomponent"))
                .andExpect(flash().attribute("message", "Tjänstekomponent med id 0 skapad"));
    }

    @Test
    public void validateIncorrectHsaIdTest () throws Exception {
        mockMvc.perform(post("/logiskAdress/create")
                        .param("hsaId", "HSA:ID-123")
                        .param("beskrivning", "Ogiltigt tecken")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1));
    }

    @Test
    public void validateCorrectNamnrymdTest () throws Exception {
        mockMvc.perform(post("/tjanstekontrakt/create")
                        .param("namnrymd", "urn:riv:clinicalprocess:activity:actions:GetActivitiesResponder:1")
                        .param("beskrivning", "Godkända tecken")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tjanstekontrakt"))
                .andExpect(flash().attribute("message", stringContainsInOrder("Tjänstekontrakt", "skapad")));
    }

    @Test
    public void validateIncorrectNamnrymdTest () throws Exception {
        mockMvc.perform(post("/tjanstekontrakt/create")
                        .param("namnrymd", "urn:riv:clinicalprocess:activity:actions: GetActivitiesResponder:1")
                        .param("beskrivning", "Innehåller mellanslag")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().errorCount(1));
    }

    // Filter specific tests

    @Test
    public void createFilterSetsAnropsbehorighetAndServiceDomainTest () throws Exception {
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
                .andExpect(flash().attribute("message", stringContainsInOrder("Filter", "skapad")));

        verify(filterServiceMock, times(1))
                .add(argThat(a -> a.getAnropsbehorighet() != null && a.getServicedomain() != null), any(String.class));
    }

    @Test
    public void createFilterIllegalIdTest () throws Exception {
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
        when(filterServiceMock.getId(any(Filter.class))).thenReturn(42L);
        Anropsbehorighet mockAb = new Anropsbehorighet();
        mockAb.setId(42L);
        when(anropsBehorighetServiceMock.getAnropsbehorighet(5L, 2L, 14L))
                .thenReturn(mockAb);
        when(filterServiceMock.update(any(), any())).thenAnswer(i -> i.getArguments()[0]);

        mockMvc.perform(post("/filter/update")
                        .param("id", "42")
                        .param("version", "1")
                        .param("tjanstekontrakt", "14")
                        .param("tjanstekonsument", "2")
                        .param("logiskAdress", "5")
                        .param("servicedomain", "urn:a.b.c")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/filter"))
                .andExpect(flash().attribute("message", "Filter med id 42 uppdaterad"));

        verify(filterServiceMock, times(1))
                .update(argThat(a -> a.getAnropsbehorighet().getId() == 42 && a.getServicedomain() == "urn:a.b.c"), any(String.class));
    }

    @Test
    public void updateFilterNoAnropsbehorighetTest () throws Exception {
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

    @Test
    public void deleteTest () throws Exception {
        when(vagvalServiceMock.findById(4)).thenReturn(Optional.of(new Vagval()));
        when(vagvalServiceMock.delete(eq(4L), anyString())).thenReturn(true);

        mockMvc.perform(post("/vagval/delete")
                        .param("id", "4")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vagval"))
                .andExpect(flash().attribute("message", "Vägval med id 4 borttagen"));
        verify(vagvalServiceMock, times(1)).delete(4L,"TEST_USER");
    }

    @Test
    public void deleteConstraintFailedTest () throws Exception {
        when(anropsAdressServiceMock.findById(8L)).thenReturn(Optional.of(new AnropsAdress()));
        when(anropsAdressServiceMock.delete(eq(8L), anyString())).thenReturn(false);

        mockMvc.perform(post("/anropsadress/delete")
                        .param("id", "8")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anropsadress"))
                .andExpect(flash().attribute("errors",
                        "Anropsadress kunde inte tas bort på grund av användning i annan konfiguration"));
        verify(anropsAdressServiceMock, times(1)).delete(8L,"TEST_USER");
    }

    @Test
    public void confirmDeleteChecksIfUserAllowedTest() throws Exception {
        Anropsbehorighet mockAb = Mockito.mock(Anropsbehorighet.class);
        when(mockAb.toString()).thenReturn("TEST_AB");
        when(anropsBehorighetServiceMock.findById(anyLong())).thenReturn(Optional.of(mockAb));
        when(anropsBehorighetServiceMock.isUserAllowedToDelete(any(Anropsbehorighet.class), anyString())).thenReturn(true);

        mockMvc.perform(post("/anropsbehorighet/confirmDelete")
                        .param("toDelete", "2")
                        .param("toDelete", "9")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(content().string(containsString("TEST_AB")));
        verify(anropsBehorighetServiceMock, times(2))
                .isUserAllowedToDelete(any(Anropsbehorighet.class),eq("TEST_USER"));
    }

    @Test
    public void confirmDeleteWithNothingSelectedTest() throws Exception {
        mockMvc.perform(post("/anropsbehorighet/confirmDelete")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errors"))
                .andExpect(content().string(containsString("Inget att ta bort")));
    }

    @Test
    public void confirmDeleteWhenNotAllowed() throws Exception {
        Anropsbehorighet mockAb = Mockito.mock(Anropsbehorighet.class);
        when(mockAb.toString()).thenReturn("TEST_AB");
        when(anropsBehorighetServiceMock.findById(anyLong())).thenReturn(Optional.of(mockAb));
        when(anropsBehorighetServiceMock.isUserAllowedToDelete(any(Anropsbehorighet.class), anyString())).thenReturn(false);

        mockMvc.perform(post("/anropsbehorighet/confirmDelete")
                        .param("toDelete", "2")
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("errors"))
                .andExpect(content().string(containsString("objekt kommer ej tas bort")));
    }

    @Test
    public void bulkDeleteSuccessTest() throws Exception {
        when(anropsBehorighetServiceMock.delete(anyLong(), anyString())).thenReturn(true);

        mockMvc.perform(post("/anropsbehorighet/bulkDelete")
                        .param("toDelete", "2")
                        .param("toDelete", "9")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anropsbehorighet"))
                .andExpect(flash().attribute("message", "Tog bort 2 objekt."));
        verify(anropsBehorighetServiceMock, times(1)).delete(eq(2L),eq("TEST_USER"));
        verify(anropsBehorighetServiceMock, times(1)).delete(eq(9L),eq("TEST_USER"));
    }

    @Test
    public void bulkDeleteFailureTest() throws Exception {
        when(anropsBehorighetServiceMock.delete(anyLong(), anyString())).thenReturn(false);

        mockMvc.perform(post("/anropsbehorighet/bulkDelete")
                        .param("toDelete", "2")
                        .param("toDelete", "9")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anropsbehorighet"))
                .andExpect(flash().attribute("errors", "Misslyckades att ta bort 2 objekt."));
        verify(anropsBehorighetServiceMock, times(1)).delete(eq(2L),eq("TEST_USER"));
        verify(anropsBehorighetServiceMock, times(1)).delete(eq(9L),eq("TEST_USER"));
    }

    @Test
    public void bulkDeleteNothingToDeleteTest() throws Exception {
        when(anropsBehorighetServiceMock.delete(anyLong(), anyString())).thenReturn(true);

        mockMvc.perform(post("/anropsbehorighet/bulkDelete")
                ).andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/anropsbehorighet"))
                .andExpect(flash().attribute("errors", "Inget att ta bort"));

        verify(anropsBehorighetServiceMock, times(0)).delete(anyLong(),anyString());
    }
}
