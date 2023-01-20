package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AnropsAdressServiceTests {

    AnropsAdressService service;

    @Autowired
    AnropsAdressRepository repository;

    @MockBean ConfigurationService configurationService;
    @MockBean AnvandareService anvandareService;

    @BeforeEach
    public void setUp() {

        service = new AnropsAdressService(repository);
    }

    @Test
    public void testGetAnropsAdressNotFound() {
        AnropsAdress result = service.getAnropsAdress("RIVTABP1", "komp", "hej");
        assertNull(result);
    }

    @Test
    public void testGetAnropsAdressFound() {
        AnropsAdress result = service.getAnropsAdress("RIVTABP21", "EI-HSAID",
                "http://localhost:8081/skltp-ei/update-service/v1");
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(false, result.getDeleted());
    }

    @Test
    public void testHasDuplicateMatch() {

        RivTaProfil profil = new RivTaProfil();
        profil.setId(2);
        Tjanstekomponent komponent = new Tjanstekomponent();
        komponent.setId(4);
        AnropsAdress a = new AnropsAdress();
        a.setRivTaProfil(profil);
        a.setTjanstekomponent(komponent);
        a.setAdress("http://localhost:8081/skltp-ei/update-service/v1");

        boolean result = service.hasDuplicate(a);
        assertTrue(result);
    }

    @Test
    public void testHasDuplicateWhenUpdate() {

        Optional<AnropsAdress> a = service.findById(3);
        assertTrue(a.isPresent());
        AnropsAdress current = a.get();
        current.setAdress("http://localhost:8081/skltp-ei/update-service/v3");

        boolean result = service.hasDuplicate(current);
        assertFalse(result);
    }

    @Test
    public void testHasDuplicateNoMatch() {

        RivTaProfil profil = new RivTaProfil();
        profil.setId(2);
        Tjanstekomponent komponent = new Tjanstekomponent();
        komponent.setId(4);
        AnropsAdress a = new AnropsAdress();
        a.setRivTaProfil(profil);
        a.setTjanstekomponent(komponent);
        a.setAdress("http://localhost:8081/skltp-ei/update-service/v2");

        boolean result = service.hasDuplicate(a);
        assertFalse(result);
    }
}
