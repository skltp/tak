package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.web.repository.LogiskAdressRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LogiskAdressServiceTests {

    LogiskAdressService service;

    @Autowired
    LogiskAdressRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        service = new LogiskAdressService(repository);
    }

    @Test
    public void testDeleteWhenNotUsedAndNotPublished() {
        boolean result = service.delete(7L, "admin");
        assertTrue(result);
        assertFalse(service.findById(7L).isPresent());
    }

    @Test
    public void testDeleteWhenUsedInVagval() {
        boolean result = service.delete(4L, "admin");
        assertFalse(result);
        assertTrue(service.findById(4L).isPresent());
        assertFalse(service.findById(4L).get().getDeleted());
    }

    @Test
    public void testDeleteWhenUsedInAnropsbehorighet() {
        boolean result = service.delete(6L, "admin");
        assertFalse(result);
        assertTrue(service.findById(6L).isPresent());
        assertFalse(service.findById(6L).get().getDeleted());
    }
}
