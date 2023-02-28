package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        service = new RivTaProfilService(repository);
    }

    @Test
    public void testListOffset() throws Exception {
        PagedEntityList<RivTaProfil> list = service.getEntityList(5, 5, null, null, false);
        assertEquals(2, list.getContent().size());
        assertEquals(7, list.getTotalElements());
    }

    @Test
    public void testMax() throws Exception {
        PagedEntityList<RivTaProfil> list = service.getEntityList(0, 5, null, null, false);
        assertEquals(5, list.getContent().size());
        assertEquals(7, list.getTotalElements());
    }

    @Test
    public void testAddMetadata() throws Exception {
        RivTaProfil result = service.add(new RivTaProfil(), "TEST_USER");

        assertEquals("TEST_USER", result.getUpdatedBy());
        assertEquals(new Date().toString(), result.getUpdatedTime().toString());
        assertFalse(result.getDeleted());
    }

    @Test
    public void testUpdate() throws Exception {
        RivTaProfil profil = service.findById(1L).get();
        profil.setNamn("NEW_NAME");
        RivTaProfil result = service.update(profil, "TEST_USER");

        assertEquals("NEW_NAME", result.getNamn());
        assertEquals("TEST_USER", result.getUpdatedBy());
        assertEquals(new Date().toString(), result.getUpdatedTime().toString());
        assertFalse(result.getDeleted());
    }

    @Test
    public void testDeleteUnknownId() throws Exception {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
           service.delete(99L, "admin");
        });
        assertEquals("RIV-TA-profil med id 99 hittades ej", exception.getMessage());
    }

    @Test
    public void testDeleteWhenNotPublished() throws Exception {
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
