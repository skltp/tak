package se.skltp.tak.web.service;

import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.web.repository.VagvalRepository;

import java.sql.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}
