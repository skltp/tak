package se.skltp.tak.web.service;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.VagvalRepository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class VagvalServiceTests {

    VagvalService service;

    @Autowired
    VagvalRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        service = new VagvalService(repository);
    }

    @Test
    public void testDefaultDates() {
        Vagval vv = service.createEntity();
        assertNotNull(vv.getFromTidpunkt());
        assertNotNull(vv.getTomTidpunkt());
        assertTrue(vv.getTomTidpunkt().after(vv.getFromTidpunkt()));
    }

    @Test
    public void testGetId() {
        Vagval vv = new Vagval();
        vv.setId(9L);
        long result = service.getId(vv);
        assertEquals(9, result);
    }

    @Test
    public void testGetIdForNull() {
        long result = service.getId(null);
        assertEquals(0, result);
    }

    @Test
    public void testGetVagvalNotFound() {
        List<Vagval> result = service.getVagval("HSAID1", "KONTRAKT1",
                Date.valueOf("2020-12-24"), Date.valueOf("2120-12-24"));
        assertNotNull(result);
        assertThat(result, IsEmptyCollection.empty());
    }

    @Test
    public void testGetVagvalFound() {
        List<Vagval> result = service.getVagval("5565594230", "urn:riv:itintegration:engagementindex:FindContentResponder:1",
                Date.valueOf("2020-12-24"), Date.valueOf("2120-12-24"));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetVagvalFilteredByDate() {
        List<Vagval> result = service.getVagval("5565594230", "urn:riv:itintegration:engagementindex:FindContentResponder:1",
                Date.valueOf("2010-01-01"), Date.valueOf("2012-12-31"));
        assertNotNull(result);
        assertThat(result, IsEmptyCollection.empty());
    }

    @Test
    public void testFilterListTotalPages() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("logiskAdress", "equals", "5565594230"));
        PagedEntityList<Vagval> result = service.getEntityList(0, 10, filters, null, false);
        assertNotNull(result);
        assertEquals(7, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    public void testFilterListFullPages() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("rivTaProfil", "contains", "1"));
        PagedEntityList<Vagval> result = service.getEntityList(0, 5, filters, null, false);
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        assertEquals(11, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
    }

    @Test
    public void testHasOverlappingDuplicateNoMatch() {
        Vagval vv = new Vagval();
        vv.setLogiskAdress(new LogiskAdress());
        vv.setAnropsAdress(new AnropsAdress());
        vv.setTjanstekontrakt(new Tjanstekontrakt());
        vv.setFromTidpunkt(Date.valueOf("2010-01-01"));
        vv.setTomTidpunkt(Date.valueOf("2030-12-31"));

        boolean result = service.hasOverlappingDuplicate(vv);
        assertFalse(result);
    }

    @Test
    public void testHasOverlappingDuplicateMatch() {
        Vagval vv = new Vagval();
        vv.setLogiskAdress(new LogiskAdress());
        vv.getLogiskAdress().setId(5);
        vv.setAnropsAdress(new AnropsAdress());
        vv.setTjanstekontrakt(new Tjanstekontrakt());
        vv.getTjanstekontrakt().setId(14);
        vv.setFromTidpunkt(Date.valueOf("2010-01-01"));
        vv.setTomTidpunkt(Date.valueOf("2030-12-31"));

        boolean result = service.hasOverlappingDuplicate(vv);
        assertTrue(result);
    }

    @Test
    public void testHasOverlappingDuplicateOnUpdate() {
        Vagval vv = new Vagval();
        vv.setId(7); // Existing vagval being updated, shall not mark itself a duplicate
        vv.setLogiskAdress(new LogiskAdress());
        vv.getLogiskAdress().setId(5);
        vv.setAnropsAdress(new AnropsAdress());
        vv.setTjanstekontrakt(new Tjanstekontrakt());
        vv.getTjanstekontrakt().setId(14);
        vv.setFromTidpunkt(Date.valueOf("2010-01-01"));
        vv.setTomTidpunkt(Date.valueOf("2030-12-31"));

        boolean result = service.hasOverlappingDuplicate(vv);
        assertFalse(result);
    }

    @Test
    public void testOrderByTomTidpunkt() {
        PagedEntityList result = service.getEntityList(0, 20, new ArrayList<>(), "tomTidpunkt", false);

        assertNotNull(result);
        assertEquals(12, result.getContent().size());

        Vagval first = (Vagval) result.getContent().get(0);
        assertEquals("2014-08-24", first.getTomTidpunkt().toString());
        Vagval last = (Vagval) result.getContent().get(10);
        assertEquals("2113-08-24", last.getTomTidpunkt().toString());
    }

    @Test
    public void testOrderByFromTidpunktDesc() {
        PagedEntityList result = service.getEntityList(0, 20, new ArrayList<>(), "fromTidpunkt", true);

        assertNotNull(result);
        assertEquals(12, result.getContent().size());

        Vagval first = (Vagval) result.getContent().get(0);
        assertEquals("2025-05-24", first.getFromTidpunkt().toString());
        Vagval last = (Vagval) result.getContent().get(10);
        assertEquals("2013-05-24", last.getFromTidpunkt().toString());
    }

    @Test
    public void testOrderByNestedRivTaProfil() {
        PagedEntityList result = service.getEntityList(0, 20, new ArrayList<>(), "anropsAdress_rivTaProfil", false);

        assertNotNull(result);
        assertEquals(12, result.getContent().size());

        Vagval first = (Vagval) result.getContent().get(0);
        assertEquals("RIVTABP20", first.getAnropsAdress().getRivTaProfil().getNamn());
        Vagval last = (Vagval) result.getContent().get(10);
        assertEquals("RIVTABP21", last.getAnropsAdress().getRivTaProfil().getNamn());
    }
}
