package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.repository.AnvandareRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnvandareServiceTests {

    AnvandareService service;

    @Mock
    AnvandareRepository repository;

    @BeforeEach
    public void setUp() {
        Anvandare anvandare1 = new Anvandare();
        anvandare1.setId(1L);
        anvandare1.setAnvandarnamn("foo");
        anvandare1.setLosenord("bar");
        Anvandare anvandare2 = new Anvandare();
        anvandare2.setId(2L);
        anvandare2.setAnvandarnamn("bob");
        anvandare2.setLosenord("skltp");

        MockitoAnnotations.openMocks(this);
        when(repository.findById(1L)).thenReturn(Optional.of(anvandare1));
        when(repository.findById(2L)).thenReturn(Optional.of(anvandare2));
        when(repository.save(any(Anvandare.class))).thenAnswer(i -> i.getArguments()[0]);

        service = new AnvandareService(repository);
    }

    @Test
    public void testSuccessfulDelete() throws Exception {
        boolean result = service.delete(1L, 0L);
        assertTrue(result);
    }

    @Test
    public void testVersionMismatchDelete() throws Exception {
        boolean result = service.delete(2L, 1L);
        assertFalse(result);
    }

    @Test
    public void testTryToAddWithEmptyPassword() throws Exception {
        Anvandare anvandare = new Anvandare();
        anvandare.setAnvandarnamn("TESTANVÄNDARE");
        anvandare.setLosenord("");

        assertThrows(IllegalArgumentException.class, () -> {
            service.add(anvandare);
        });
    }

    @Test
    public void testUpdateUsernamePassword() throws Exception {
        Anvandare anvandare = new Anvandare();
        anvandare.setId(1L);
        anvandare.setAnvandarnamn("TESTANVÄNDARE");
        anvandare.setLosenord("skltp");

        Anvandare result = service.update(anvandare);
        assertEquals(1L, result.getId());
        assertEquals("TESTANVÄNDARE", result.getAnvandarnamn());
        assertEquals("3e1a694fd3a41e113dfbd4bf108cdee44206d1b1", result.getLosenordHash());
    }

    @Test
    public void testUpdateWithoutPassword() throws Exception {
        Anvandare anvandare = new Anvandare();
        anvandare.setId(2L);
        anvandare.setAnvandarnamn("bobby");
        anvandare.setAdministrator(true);

        Anvandare result = service.update(anvandare);
        assertEquals(2L, result.getId());
        assertEquals("bobby", result.getAnvandarnamn());
        assertTrue(result.getAdministrator());
        assertEquals("3e1a694fd3a41e113dfbd4bf108cdee44206d1b1", result.getLosenordHash());
    }

    @Test
    public void testUpdateUnknownUser() throws Exception {
        Anvandare anvandare = new Anvandare();
        anvandare.setId(5L);
        anvandare.setAnvandarnamn("nobody");

        assertThrows(IllegalArgumentException.class, () -> {
            service.update(anvandare);
        });
    }

}
