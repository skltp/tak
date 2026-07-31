/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import java.util.ArrayList;
import java.util.stream.Stream;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean ConfigurationService configurationService;
    @MockitoBean AnvandareService anvandareService;

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

    private static Stream<Arguments> hasOverlappingDuplicateTestCases() {
        return Stream.of(
            // No match - empty IDs
            Arguments.of(0L, 0, 0, 0, false),
            // Match - existing combination
            Arguments.of(0L, 1, 2, 10, true),
            // No match on update - same entity being updated
            Arguments.of(1L, 1, 2, 10, false)
        );
    }

    @ParameterizedTest
    @MethodSource("hasOverlappingDuplicateTestCases")
    public void testHasOverlappingDuplicate(long entityId, int logiskAdressId, int tjanstekonsumentId, int tjanstekontraktId, boolean expectedResult) {
        Anropsbehorighet ab = new Anropsbehorighet();
        if (entityId > 0) {
            ab.setId(entityId);
        }
        ab.setLogiskAdress(new LogiskAdress());
        if (logiskAdressId > 0) {
            ab.getLogiskAdress().setId(logiskAdressId);
        }
        ab.setTjanstekonsument(new Tjanstekomponent());
        if (tjanstekonsumentId > 0) {
            ab.getTjanstekonsument().setId(tjanstekonsumentId);
        }
        ab.setTjanstekontrakt(new Tjanstekontrakt());
        if (tjanstekontraktId > 0) {
            ab.getTjanstekontrakt().setId(tjanstekontraktId);
        }
        ab.setFromTidpunkt(Date.valueOf("2010-01-01"));
        ab.setTomTidpunkt(Date.valueOf("2030-12-31"));

        boolean result = service.hasOverlappingDuplicate(ab);
        assertEquals(expectedResult, result);
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

    private static Stream<Arguments> filterListTestCases() {
        return Stream.of(
            Arguments.of("integrationsavtal", FilterCondition.EQUALS, "I1", 4),
            Arguments.of("tjanstekonsument.hsaId", FilterCondition.STARTS_WITH, "TP", 9),
            Arguments.of("tjanstekontrakt.namnrymd", FilterCondition.CONTAINS, "PingResponder", 3),
            Arguments.of("logiskAdress.hsaId", FilterCondition.EQUALS, "5565594230", 4)
        );
    }

    @ParameterizedTest
    @MethodSource("filterListTestCases")
    public void testFilterList(String field, FilterCondition condition, String value, int expectedSize) {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter(field, condition, value));
        PagedEntityList<Anropsbehorighet> result = service.getEntityList(0, 10, filters, null, false, false);
        assertNotNull(result);
        assertEquals(expectedSize, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }
}
