package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.web.repository.LogiskAdressRepository;
import se.skltp.tak.web.repository.TjanstekomponentRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TjanstekomponentServiceTests {

    TjanstekomponentService service;

    @Autowired
    TjanstekomponentRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        service = new TjanstekomponentService(repository);
    }

    @Test
    public void testDeleteWhenNotUsedAndNotPublished() {
        boolean result = service.delete(9L, "admin");
        assertTrue(result);
        assertFalse(service.findById(9L).isPresent());
    }

    @Test
    public void testDeleteWhenUsedInAnropsadress() {
        boolean result = service.delete(7L, "admin");
        assertFalse(result);
        assertTrue(service.findById(7L).isPresent());
        assertFalse(service.findById(7L).get().getDeleted());
    }

    @Test
    public void testDeleteWhenUsedInAnropsbehorighet() {
        boolean result = service.delete(8L, "admin");
        assertFalse(result);
        assertTrue(service.findById(8L).isPresent());
        assertFalse(service.findById(8L).get().getDeleted());
    }
}
