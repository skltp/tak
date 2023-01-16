package se.skltp.tak.web.service;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.web.repository.AnropsBehorighetRepository;

import java.sql.Date;
import java.util.List;

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
        service = new AnropsBehorighetService(repository);
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
}
