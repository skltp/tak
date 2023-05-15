package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.web.dto.PagedEntityList;
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

    @Test
    public void testDeleteWhenNotUsedAndNotPublished() {
        boolean result = service.delete(9L, "admin");
        assertTrue(result);
        assertFalse(service.findById(9L).isPresent());
    }

    @Test
    public void testDeleteWhenUsedInVagval() {
        boolean result = service.delete(6L, "admin");
        assertFalse(result);
        assertTrue(service.findById(6L).isPresent());
        assertFalse(service.findById(6L).get().getDeleted());
    }

    @Test
    public void testDeleteWhenUsedInVagvalDeletedByOtherUser() {
        boolean result = service.delete(2L, "OTHER_USER");
        assertFalse(result);
        assertTrue(service.findById(2L).isPresent());
        assertFalse(service.findById(2L).get().getDeleted());
    }

    @Test
    public void testDeleteWhenUsedInVagvalDeletedBySameUser() {
        boolean result = service.delete(2L, "admin");
        assertTrue(result);
        assertTrue(service.findById(2L).isPresent());
        assertTrue(service.findById(2L).get().getDeleted());
    }

    @Test
    public void getUnmatchedEntityList() {
        PagedEntityList<AnropsAdress> result = service.getUnmatchedEntityList(0, 10, "adress", false);
        assertNotNull(result);
        assertEquals(3, result.getSize());
        AnropsAdress first = (AnropsAdress) result.getContent().get(0);
        assertEquals("http://localhost:10000/test/Ping_Service", first.getAdress());
    }

    @Test
    public void getUnmatchedEntityListMultiPage() {
        PagedEntityList<AnropsAdress> result = service.getUnmatchedEntityList(0, 2, "adress", false);
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getSize());
        assertEquals(2, result.getTotalPages());
    }
}
