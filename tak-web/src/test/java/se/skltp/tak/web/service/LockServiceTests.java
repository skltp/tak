package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.web.entity.Locktb;
import se.skltp.tak.web.repository.LockRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LockServiceTests {

    LockService service;

    @Autowired
    LockRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        service = new LockService(repository);
    }

    @Test
    public void testRetrieveLock() {
        Locktb locktb = service.retrieveLock();
        assertNotNull(locktb);
        assertEquals(1, locktb.getLocked());
    }

    @Test
    public void testRetrieveLockTwice() {
        Locktb locktb = service.retrieveLock();
        assertNotNull(locktb);
        assertEquals(1, locktb.getLocked());
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            service.retrieveLock();
        });
    }

    @Test
    public void testRetrieveReleaseRetrieveLock() {
        Locktb locktb = service.retrieveLock();
        assertNotNull(locktb);

        service.releaseLock(locktb);
        Locktb locktb2 = service.retrieveLock();
        assertNotNull(locktb2);
    }
}
