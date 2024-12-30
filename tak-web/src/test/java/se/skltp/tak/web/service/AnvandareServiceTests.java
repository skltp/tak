package se.skltp.tak.web.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.repository.AnvandareRepository;
import se.skltp.tak.web.util.Sha1PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AnvandareServiceTests {

    AnvandareService service;

    @Mock
    AnvandareRepository repository;

    PasswordEncoder passwordEncoder;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        // Initialize passwordEncoder as a real instance
        passwordEncoder = new Sha1PasswordEncoder();

        Anvandare anvandare1 = new Anvandare();
        anvandare1.setId(1L);
        anvandare1.setAnvandarnamn("foo");
        anvandare1.setLosenordHash(passwordEncoder.encode("bar"));

        Anvandare anvandare2 = new Anvandare();
        anvandare2.setId(2L);
        anvandare2.setAnvandarnamn("bob");
        anvandare2.setLosenordHash(passwordEncoder.encode("skltp"));

        closeable = MockitoAnnotations.openMocks(this);

        // Mock repository behavior
        when(repository.findById(1L)).thenReturn(Optional.of(anvandare1));
        when(repository.findById(2L)).thenReturn(Optional.of(anvandare2));
        when(repository.save(any(Anvandare.class))).thenAnswer(i -> i.getArguments()[0]);
        when(repository.saveAndFlush(any(Anvandare.class))).thenAnswer(i -> i.getArguments()[0]);

        service = new AnvandareService(repository, passwordEncoder);

    }

    @AfterEach
    public void tearDown() throws Exception {
        // Stäng alla öppna mockar och rensa resurser
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void testSuccessfulDelete() {
        boolean result = service.delete(1L, 0L);
        assertTrue(result);
    }

    @Test
    void testVersionMismatchDelete() {
        boolean result = service.delete(2L, 1L);
        assertFalse(result);
    }

    @Test
    void testTryToAddWithEmptyPassword() {
        Anvandare anvandare = new Anvandare();
        anvandare.setAnvandarnamn("TESTANVÄNDARE");
        anvandare.setLosenordHash("");

        assertThrows(IllegalArgumentException.class, () ->
                service.add(anvandare));
    }

    @Test
    void testUpdateUsernamePassword() {
        Anvandare anvandare = new Anvandare();
        anvandare.setId(1L);
        anvandare.setAnvandarnamn("TESTANVÄNDARE");
        anvandare.setLosenord("skltp");

        Anvandare result = service.update(anvandare);
        assertEquals(1L, result.getId());
        assertEquals("TESTANVÄNDARE", result.getAnvandarnamn());
        assertTrue(passwordEncoder.matches("skltp", result.getLosenordHash()));
        assertEquals("3e1a694fd3a41e113dfbd4bf108cdee44206d1b1", result.getLosenordHash());
    }

    @Test
    void testUpdateWithoutPassword() {
        Anvandare anvandare = new Anvandare();
        anvandare.setId(2L);
        anvandare.setAnvandarnamn("bobby");
        anvandare.setAdministrator(true);

        Anvandare result = service.update(anvandare);
        assertEquals(2L, result.getId());
        assertEquals("bobby", result.getAnvandarnamn());
        assertTrue(result.getAdministrator());
        assertTrue(passwordEncoder.matches("skltp", result.getLosenordHash()));
        assertEquals("3e1a694fd3a41e113dfbd4bf108cdee44206d1b1", result.getLosenordHash());
    }

    @Test
    void testUpdateUnknownUser() {
        Anvandare anvandare = new Anvandare();
        anvandare.setId(5L);
        anvandare.setAnvandarnamn("nobody");

        assertThrows(IllegalArgumentException.class, () ->
                service.update(anvandare));
    }

}
