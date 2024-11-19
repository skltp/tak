package se.skltp.tak.web.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.FilterCategorizationRepository;

import java.util.Optional;
import se.skltp.tak.web.repository.QueryGeneratorImpl;

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
        QueryGenerator<Filtercategorization> queryGenerator = new QueryGeneratorImpl<>();
        service = new FilterCategorizationService(repository, queryGenerator);
    }

    @Test
    public void testListFiltersDeleted() {
        List<ListFilter> filters = new ArrayList<>();
        PagedEntityList<Filtercategorization> list = service.getEntityList(0, 10, filters, null, false, false);
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

    @Test
    public void testFilterListCategory_OneDeleted() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("category", FilterCondition.EQUALS, "Category c1"));
        PagedEntityList<Filtercategorization> result = service.getEntityList(0, 10, filters, null, false, false);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    public void testFilterListServicedomainContains() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("filter.servicedomain", FilterCondition.CONTAINS, "GetItems"));
        PagedEntityList<Filtercategorization> result = service.getEntityList(0, 10, filters, null, false, false);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }
}
