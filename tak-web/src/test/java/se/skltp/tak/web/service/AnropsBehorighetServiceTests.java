package se.skltp.tak.web.service;

import java.util.ArrayList;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.AnropsBehorighetRepository;

import java.sql.Date;
import java.util.List;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.QueryGeneratorImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AnropsBehorighetServiceTests {

    AnropsBehorighetService service;

    @Autowired
    AnropsBehorighetRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        QueryGenerator<Anropsbehorighet> queryGenerator = new QueryGeneratorImpl<>();
        service = new AnropsBehorighetService(repository, queryGenerator);
    }

    @Test
    public void testDefaultDates() {
        Anropsbehorighet ab = service.createEntity();
        assertNotNull(ab.getFromTidpunkt());
        assertNotNull(ab.getTomTidpunkt());
        assertTrue(ab.getTomTidpunkt().after(ab.getFromTidpunkt()));
    }

    @Test
    public void testGetAnropsbehorighetNotFound() {
        List<Anropsbehorighet> result = service.getAnropsbehorighet("HSAID1", "KONSUMENT1", "hej",
                Date.valueOf("2020-12-24"), Date.valueOf("2120-12-24"));
        assertNotNull(result);
        assertThat(result, IsEmptyCollection.empty());
    }

    @Test
    public void testGetAnropsbehorighetFound() {
        List<Anropsbehorighet> result = service.getAnropsbehorighet("HSA-VKM345", "TP",
                "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1", Date.valueOf("2020-12-24"), Date.valueOf("2120-12-24"));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetAnropsbehorighetFilteredByDate() {
        List<Anropsbehorighet> result = service.getAnropsbehorighet("HSA-VKM345", "TP",
                "urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1", Date.valueOf("2010-01-01"), Date.valueOf("2012-12-31"));
        assertNotNull(result);
        assertThat(result, IsEmptyCollection.empty());
    }

    @Test
    public void testGetAnropsbehorighetByIds() {
        Anropsbehorighet result = service.getAnropsbehorighet(2, 2, 10);
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("I1", result.getIntegrationsavtal());
        assertEquals("HSA-VKM345", result.getLogiskAdress().getHsaId());
    }

    @Test
    public void testHasOverlappingDuplicateNoMatch() {
        Anropsbehorighet ab = new Anropsbehorighet();
        ab.setLogiskAdress(new LogiskAdress());
        ab.setTjanstekonsument(new Tjanstekomponent());
        ab.setTjanstekontrakt(new Tjanstekontrakt());
        ab.setFromTidpunkt(Date.valueOf("2010-01-01"));
        ab.setTomTidpunkt(Date.valueOf("2030-12-31"));

        boolean result = service.hasOverlappingDuplicate(ab);
        assertFalse(result);
    }

    @Test
    public void testHasOverlappingDuplicateMatch() {
        Anropsbehorighet ab = new Anropsbehorighet();
        ab.setLogiskAdress(new LogiskAdress());
        ab.getLogiskAdress().setId(1);
        ab.setTjanstekonsument(new Tjanstekomponent());
        ab.getTjanstekonsument().setId(2);
        ab.setTjanstekontrakt(new Tjanstekontrakt());
        ab.getTjanstekontrakt().setId(10);
        ab.setFromTidpunkt(Date.valueOf("2010-01-01"));
        ab.setTomTidpunkt(Date.valueOf("2030-12-31"));

        boolean result = service.hasOverlappingDuplicate(ab);
        assertTrue(result);
    }

    @Test
    public void testHasOverlappingDuplicateOnUpdate() {
        Anropsbehorighet ab = new Anropsbehorighet();
        ab.setId(1); // Existing AB being updated, shall not mark itself a duplicate
        ab.setLogiskAdress(new LogiskAdress());
        ab.getLogiskAdress().setId(1);
        ab.setTjanstekonsument(new Tjanstekomponent());
        ab.getTjanstekonsument().setId(2);
        ab.setTjanstekontrakt(new Tjanstekontrakt());
        ab.getTjanstekontrakt().setId(10);
        ab.setFromTidpunkt(Date.valueOf("2010-01-01"));
        ab.setTomTidpunkt(Date.valueOf("2030-12-31"));

        boolean result = service.hasOverlappingDuplicate(ab);
        assertFalse(result);
    }

    @Test
    public void testDeleteWhenNotUsedButPublished() {
        boolean result = service.delete(4L, "admin");
        assertTrue(result);
        assertTrue(service.findById(4L).isPresent());
        assertTrue(service.findById(4L).get().getDeleted());
    }

    @Test
    public void testDeleteWhenUsedInFilter() {
        boolean result = service.delete(7L, "admin");
        assertFalse(result);
        assertTrue(service.findById(7L).isPresent());
        assertFalse(service.findById(7L).get().getDeleted());
    }

    @Test
    public void testFilterListIntegrationsavtalEquals() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("integrationsavtal", FilterCondition.EQUALS, "I1"));
        PagedEntityList<Anropsbehorighet> result = service.getEntityList(0, 10, filters, null, false, false);
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    public void testFilterListTjanstekonsumentHsaIdStarts() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("tjanstekonsument.hsaId", FilterCondition.STARTS_WITH, "TP"));
        PagedEntityList<Anropsbehorighet> result = service.getEntityList(0, 10, filters, null, false, false);
        assertNotNull(result);
        assertEquals(8, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    public void testFilterListTjanstekontraktNamnrymdContains() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("tjanstekontrakt.namnrymd", FilterCondition.CONTAINS, "PingResponder"));
        PagedEntityList<Anropsbehorighet> result = service.getEntityList(0, 10, filters, null, false, false);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    public void testFilterListLogiskAdressEquals() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("logiskAdress.hsaId", FilterCondition.EQUALS, "5565594230"));
        PagedEntityList<Anropsbehorighet> result = service.getEntityList(0, 10, filters, null, false, false);
        assertNotNull(result);
        assertEquals(4, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }
}
