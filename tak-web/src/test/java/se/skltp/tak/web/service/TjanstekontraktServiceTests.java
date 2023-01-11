package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.TjanstekontraktRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TjanstekontraktServiceTests {

    TjanstekontraktService service;

    @Autowired
    TjanstekontraktRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        service = new TjanstekontraktService(repository);
    }

    @Test
    public void testGetTjanstekontraktByNamnrymdNotFound() {
        Tjanstekontrakt result = service.getTjanstekontraktByNamnrymd("wrong");
        assertNull(result);
    }

    @Test
    public void testGetTjanstekontraktByNamnrymdFound() {
        Tjanstekontrakt result = service.getTjanstekontraktByNamnrymd("urn:riv:itintegration:engagementindex:FindContentResponder:1");
        assertNotNull(result);
        assertEquals(14L, result.getId());
        assertEquals(false, result.getDeleted());
    }

    @Test
    public void testUnfilteredList() {
        PagedEntityList<Tjanstekontrakt> result = service.getEntityList(0, 100, new ArrayList<>());
        assertNotNull(result);
        assertEquals(9, result.getContent().size());
    }

    @Test
    public void testFilterListNamnrymdContainsCaseInsensitive() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("namnrymd", "contains", "servicecontract"));
        PagedEntityList<Tjanstekontrakt> result = service.getEntityList(0, 100, filters);
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    public void testFilterListBeskrivningBegins() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", "begins", "U"));
        PagedEntityList<Tjanstekontrakt> result = service.getEntityList(0, 100, filters);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testFilterListMajorVersionEquals() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("majorVersion", "equals", "1"));
        PagedEntityList<Tjanstekontrakt> result = service.getEntityList(0, 100, filters);
        assertNotNull(result);
        assertEquals(9, result.getContent().size());
    }
}
