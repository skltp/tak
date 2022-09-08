package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class RivTaProfilServiceTests {

    RivTaProfilService service;

    @Mock
    RivTaProfilRepository repository;

    @BeforeEach
    public void setUp() {
        RivTaProfil profil1 = new RivTaProfil();
        profil1.setId(1);

        RivTaProfil profil2 = new RivTaProfil();
        profil2.setId(2);
        profil2.setPubVersion("42");
        profil2.setDeleted(null);

        RivTaProfil profil3 = new RivTaProfil();
        profil3.setId(3);

        List<RivTaProfil> profilList = new ArrayList<>();
        profilList.add(profil1);
        profilList.add(profil2);
        profilList.add(profil3);

        MockitoAnnotations.openMocks(this);
        when(repository.findById(1L)).thenReturn(Optional.of(profil1));
        when(repository.findAll()).thenReturn(profilList);
        when(repository.save(any(RivTaProfil.class))).thenAnswer(i -> i.getArguments()[0]);
        service = new RivTaProfilService(repository);
    }

    @Test
    public void testListFiltersDeleted() throws Exception {
        PagedEntityList<RivTaProfil> list = service.getEntityList(0, 10);
        List<RivTaProfil> content = list.getContent();
        assertEquals(2, content.size());
        assertFalse(content.get(0).isDeletedInPublishedVersion());
        assertFalse(content.get(1).isDeletedInPublishedVersion());
    }

    @Test
    public void testListOffset() throws Exception {
        PagedEntityList<RivTaProfil> list = service.getEntityList(1, 10);
        assertEquals(1, list.getContent().size());
        assertEquals(2, list.getTotalElements());
    }

    @Test
    public void testMax() throws Exception {
        PagedEntityList<RivTaProfil> list = service.getEntityList(0, 1);
        assertEquals(1, list.getContent().size());
        assertEquals(2, list.getTotalElements());
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
        verify(repository, times(1)).save(any(RivTaProfil.class));
    }

    @Test
    public void testMarkDeleted() throws Exception {
        boolean result = service.delete(1L, "TEST_USER");
        assertTrue(result);
        verify(repository, times(1)).save(any(RivTaProfil.class));
    }
}
