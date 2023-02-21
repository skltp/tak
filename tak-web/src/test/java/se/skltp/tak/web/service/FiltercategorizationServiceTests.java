package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.FilterCategorizationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class FiltercategorizationServiceTests {

    FilterCategorizationService service;

    @Autowired
    FilterCategorizationRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        service = new FilterCategorizationService(repository);
    }

    @Test
    public void testListFiltersDeleted() {

        PagedEntityList<Filtercategorization> list = service.getEntityList(0, 10, null);
        assertEquals(3, list.getContent().size());
        assertEquals(3, list.getTotalElements());
    }

    @Test
    public void testHasDuplicateMatch() {
        Filtercategorization fc = new Filtercategorization();
        fc.setFilter(new Filter());
        fc.getFilter().setId(4);
        fc.setCategory("Category c2");
        boolean result = service.hasDuplicate(fc);
        assertTrue(result);
    }

    @Test
    public void testHasDuplicateWhenUpdate() {

        Optional<Filtercategorization> a = service.findById(2);
        assertTrue(a.isPresent());
        Filtercategorization current = a.get();
        current.setCategory("New Category");

        boolean result = service.hasDuplicate(current);
        assertFalse(result);
    }

    @Test
    public void testHasDuplicateNoMatch() {
        Filtercategorization fc = new Filtercategorization();
        fc.setFilter(new Filter());
        fc.getFilter().setId(4);
        fc.setCategory("Category c7");
        boolean result = service.hasDuplicate(fc);
        assertFalse(result);
    }
}
