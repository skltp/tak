package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.FilterRepository;

import java.util.Optional;
import se.skltp.tak.web.repository.QueryGeneratorImpl;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class FilterServiceTests {

    FilterService service;

    @Autowired
    FilterRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;


    @BeforeEach
    public void setUp() {
        QueryGenerator<Filter> queryGenerator = new QueryGeneratorImpl<>();
        service = new FilterService(repository, queryGenerator);
    }

    @Test
    public void testHasDuplicateMatch() {
        Filter f = new Filter();
        f.setAnropsbehorighet(new Anropsbehorighet());
        f.getAnropsbehorighet().setId(8);
        f.setServicedomain("urn:riv:itintegration:registry:GetItems");
        boolean result = service.hasDuplicate(f);
        assertTrue(result);
    }

    @Test
    public void testHasDuplicateWhenUpdate() {

        Optional<Filter> a = service.findById(3);
        assertTrue(a.isPresent());
        Filter current = a.get();
        current.setServicedomain("urn:riv:itintegration:registry:GetItems2");

        boolean result = service.hasDuplicate(current);
        assertFalse(result);
    }

    @Test
    public void testHasDuplicateNoMatch() {
        Filter f = new Filter();
        f.setAnropsbehorighet(new Anropsbehorighet());
        f.getAnropsbehorighet().setId(8);
        f.setServicedomain("urn:riv:itintegration:registry:GetItems3");
        boolean result = service.hasDuplicate(f);
        assertFalse(result);
    }

    @Test
    public void testHasDuplicateNoAnropsbehorighet() {
        Filter f = new Filter();
        f.setServicedomain("urn:riv:itintegration:registry:GetItems3");
        assertThrows(IllegalArgumentException.class, () -> service.hasDuplicate(f));
    }

    @Test
    public void testDeleteWhenNotUsedButPublished() {
        boolean result = service.delete(1L, "admin");
        assertTrue(result);
        assertTrue(service.findById(1L).isPresent());
        assertTrue(service.findById(1L).get().getDeleted());
    }

    @Test
    public void testDeleteWhenUsedInCategorization() {
        boolean result = service.delete(3L, "admin");
        assertFalse(result);
        assertTrue(service.findById(3L).isPresent());
        assertFalse(service.findById(3L).get().getDeleted());
    }
}
