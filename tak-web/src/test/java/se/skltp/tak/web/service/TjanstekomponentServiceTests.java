package se.skltp.tak.web.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.QueryGeneratorImpl;
import se.skltp.tak.web.repository.TjanstekomponentRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        QueryGenerator<Tjanstekomponent> queryGenerator = new QueryGeneratorImpl<>();
        service = new TjanstekomponentService(repository, queryGenerator);
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


    @Test
    public void testFilterListHsaIdEquals() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("hsaId", FilterCondition.EQUALS, "5565594230"));
        PagedEntityList<Tjanstekomponent> result = service.getEntityList(0, 100, filters, null, false, false);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testFilterListBeskrivningStarts() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", FilterCondition.STARTS_WITH, "Nya producent"));
        PagedEntityList<Tjanstekomponent> result = service.getEntityList(0, 100, filters, null, false, false);
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }
}
