package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.LogiskAdressRepository;

import java.util.ArrayList;
import java.util.List;
import se.skltp.tak.web.repository.QueryGeneratorImpl;

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
        QueryGenerator<LogiskAdress> queryGenerator = new QueryGeneratorImpl<>();
        service = new LogiskAdressService(repository, queryGenerator);
    }

    @Test
    public void testFilterListBeskrivningEquals() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", FilterCondition.EQUALS, "Organisation: Inera"));
        PagedEntityList<LogiskAdress> result = service.getEntityList(0, 100, filters, null, false, false);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    public void testFilterListHSAIdStarts() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("hsaId", FilterCondition.STARTS_WITH, "HSA-VK"));
        PagedEntityList<LogiskAdress> result = service.getEntityList(0, 10, filters, null, false, false);
        assertEquals(3, result.getContent().size());
        assertEquals(1, result.getTotalPages());
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

    @Test
    public void getUnmatchedEntityListByVagvalSorted() {
        PagedEntityList<LogiskAdress> result = service.getUnmatchedEntityList(0, 10, "beskrivning", false, "Vagval");
        assertNotNull(result);
        assertEquals(3, result.getSize());
        LogiskAdress first = (LogiskAdress) result.getContent().get(0);
        LogiskAdress second = (LogiskAdress) result.getContent().get(1);
        assertEquals(null, first.getBeskrivning());
        assertEquals("Organisation: Unpublished by skltp", second.getBeskrivning());
    }

    @Test
    public void getUnmatchedEntityListByAny() {
        PagedEntityList<LogiskAdress> result = service.getUnmatchedEntityList(0, 10, null, false, "Any");
        assertNotNull(result);
        assertEquals(1, result.getSize());
        LogiskAdress first = (LogiskAdress) result.getContent().get(0);
        assertEquals("HSA-UNPUBLISHED-USER-SKLTP", first.getHsaId());
    }
}
