package se.skltp.tak.web.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RivTaProfilServiceTests {

    RivTaProfilService service;

    @Mock
    RivTaProfilRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(repository.findByDeleted(false)).thenReturn(new ArrayList<RivTaProfil>());
        when(repository.save(any(RivTaProfil.class))).thenAnswer(i -> i.getArguments()[0]);
        service = new RivTaProfilService(repository);
    }

    @Test
    public void testFilterDeleted() throws Exception {
        List<RivTaProfil> result = service.findNotDeleted();
        verify(repository, times(1)).findByDeleted(false);
    }

    @Test
    public void testAddMetadata() throws Exception {
        RivTaProfil result = service.add(new RivTaProfil(), "TEST_USER");

        assertEquals("TEST_USER", result.getUpdatedBy());
        // Check time with rounding
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        assertEquals(formatter.format(new Date()), formatter.format(result.getUpdatedTime()));
    }
}
