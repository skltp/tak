package se.skltp.tak.web.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.util.Optional;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.QueryGeneratorImpl;

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
        QueryGenerator<AnropsAdress> queryGenerator = new QueryGeneratorImpl<>();
        service = new AnropsAdressService(repository, queryGenerator);
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
    public void testGetAnropsAdressDiffersByCase() {
        AnropsAdress result = service.getAnropsAdress("RIVTABP21", "EI-HSAID",
                "http://localhost:8081/SKLTP-EI/Update-service/v1");
        assertNull(result);
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
    public void testFiltAradressContains() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("adress", FilterCondition.CONTAINS, "localhost"));
        PagedEntityList<AnropsAdress> result = service.getEntityList(0, 10, filters, null, false);
        assertNotNull(result);
        assertEquals(7, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    public void testFilterTjanstekomponentHsaIdEquals() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("tjanstekomponent.hsaId", FilterCondition.EQUALS, "EI-HSAID"));
        PagedEntityList<AnropsAdress> result = service.getEntityList(0, 10, filters, null, false);
        assertNotNull(result);
        assertEquals(6, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    public void testFilterTjanstekomponentHsaIdEquals_2pages() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("tjanstekomponent.hsaId", FilterCondition.EQUALS, "EI-HSAID"));
        PagedEntityList<AnropsAdress> result = service.getEntityList(0, 5, filters, null, false);
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    public void testFilterRivTaProfilExists() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("rivTaProfil", FilterCondition.EXISTS));
        PagedEntityList<AnropsAdress> result = service.getEntityList(0, 10, filters, null, false);
        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(1, result.getTotalPages());
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
