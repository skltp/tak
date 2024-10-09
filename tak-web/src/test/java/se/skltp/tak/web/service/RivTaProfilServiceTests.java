package se.skltp.tak.web.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.QueryGeneratorImpl;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import java.util.Date;
import java.util.Optional;

@DataJpaTest
public class RivTaProfilServiceTests {

    RivTaProfilService service;

    @Autowired
    RivTaProfilRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {
        QueryGenerator<RivTaProfil> queryGenerator = new QueryGeneratorImpl<>();
        service = new RivTaProfilService(repository, queryGenerator);
    }

    @Test
    public void testFilterListBeskrivningEquals() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", FilterCondition.EQUALS, "RIV TA BP 2.0-Published"));
        PagedEntityList<RivTaProfil> result = service.getEntityList(0, 100, filters, null, false);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testFilterListNamnStarts() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("namn", FilterCondition.STARTS_WITH, "RIVTA"));
        PagedEntityList<RivTaProfil> result = service.getEntityList(0, 100, filters, null, false);
        assertNotNull(result);
        assertEquals(7, result.getContent().size());
    }

    @Test
    public void testListOffset() {
        List<ListFilter> filters = new ArrayList<>();
        PagedEntityList<RivTaProfil> list = service.getEntityList(5, 5, filters, null, false);
        assertEquals(2, list.getContent().size());
        assertEquals(7, list.getTotalElements());
    }

    @Test
    public void testMax() {
        List<ListFilter> filters = new ArrayList<>();
        PagedEntityList<RivTaProfil> list = service.getEntityList(0, 5, filters, null, false);
        assertEquals(5, list.getContent().size());
        assertEquals(7, list.getTotalElements());
    }

    @Test
    public void testAddMetadata() {
        RivTaProfil result = service.add(new RivTaProfil(), "TEST_USER");

        assertEquals("TEST_USER", result.getUpdatedBy());
        assertEquals(new Date().toString(), result.getUpdatedTime().toString());
        assertFalse(result.getDeleted());
    }

    @Test
    public void testUpdate()  {
        RivTaProfil profil = service.findById(1L).get();
        profil.setNamn("NEW_NAME");
        RivTaProfil result = service.update(profil, "TEST_USER");

        assertEquals("NEW_NAME", result.getNamn());
        assertEquals("TEST_USER", result.getUpdatedBy());
        assertTrue(DateUtils.isSameDay(new Date(), result.getUpdatedTime()));

        assertFalse(result.getDeleted());
    }

    @Test
    public void testDeleteUnknownId()  {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
           service.delete(99L, "admin");
        });
        assertEquals("RIV-TA-profil med id 99 hittades ej", exception.getMessage());
    }

    @Test
    public void testDeleteWhenNotPublished()  {
        boolean result = service.delete(7L, "admin");
        assertTrue(result);
        Optional<RivTaProfil> after = service.findById(7L);
        assertFalse(after.isPresent());
    }

    @Test
    public void testDeleteWhenUsedInAnropsadress() {
        boolean result = service.delete(2L, "admin");
        assertFalse(result);
        assertTrue(service.findById(2L).isPresent());
        assertFalse(service.findById(2L).get().getDeleted());
    }
}
