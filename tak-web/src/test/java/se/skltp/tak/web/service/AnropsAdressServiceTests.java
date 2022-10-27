package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AnropsAdressServiceTests {

    AnropsAdressService service;

    @Autowired
    AnropsAdressRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {

        service = new AnropsAdressService(repository);
    }

    @Test
    public void testGetAnropsAdressNotFound() {
        AnropsAdress result = service.getAnropsAdress("RIVTABP1", "komp", "hej");
        assertNull(result);
    }

    @Test
    public void testGetAnropsAdressFound() {
        AnropsAdress result = service.getAnropsAdress("RIVTABP21", "EI-HSAID",
                "http://localhost:8081/skltp-ei/update-service/v1");
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(false, result.getDeleted());
    }
}
