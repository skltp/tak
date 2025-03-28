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
    public void testAddNewObjectDeletedTrue() {
        List<ListFilter> filters = new ArrayList<>();
        PagedEntityList<?> list = service.getEntityList(0, 100, filters, null, false, true);
        assertEquals(0, list.getSize());

        RivTaProfil profil = new RivTaProfil();
        profil.setBeskrivning("RIV TA BP 10.0-Published and then deleted");
        profil.setNamn("RIVTA100");
        profil.setUpdatedBy(null);
        profil.setUpdatedTime(null);
        profil.setPubVersion("1");
        profil.setDeleted(null); //setDeleted = null = true
        repository.save(profil);

        PagedEntityList<?> newList2 = service.getEntityList(0, 100, filters, null, false, true);
        assertEquals(1, newList2.getSize());
    }

    @Test
    public void testFilterShowDeletedFalse() {
        List<ListFilter> filters = new ArrayList<>();
        PagedEntityList<?> list = service.getEntityList(0, 100, filters, null, false, false);
        assertEquals(7, list.getSize());

        RivTaProfil profil = new RivTaProfil();
        profil.setBeskrivning("RIV TA BP 11.0-Published and then deleted");
        profil.setNamn("RIVTA110");
        profil.setUpdatedBy(null);
        profil.setUpdatedTime(null);
        profil.setPubVersion("1");
        profil.setDeleted(false);
        repository.save(profil);

        PagedEntityList<?> updatedList = service.getEntityList(0, 100, filters, null, false, false);
        assertEquals(8, updatedList.getSize());
    }

    @Test
    public void testFilterListOnlyMatchExactUnderscore() {
        RivTaProfil profil1 = new RivTaProfil();
        profil1.setBeskrivning("A_C"); //Ska matcha
        service.save(profil1);

        RivTaProfil profil2 = new RivTaProfil();
        profil2.setBeskrivning("ABC"); // Ska inte matcha
        service.save(profil2);

        RivTaProfil profil3 = new RivTaProfil();
        profil3.setBeskrivning("A-C"); //Ska inte matcha
        service.save(profil3);

        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", FilterCondition.CONTAINS, "A_C"));

        PagedEntityList<?> result = service.getEntityList(0, 100, filters, null, false, false);
        assertEquals(1, result.getSize()); // Endast "A_C" ska matcha

        Optional<RivTaProfil> fetchedProfil = service.findById(profil1.getId());
        assertEquals("A_C", fetchedProfil.get().getBeskrivning());
    }

    @Test
    public void testFilterListDoesNotMatchHyphenOrSpace() {
        RivTaProfil profil1 = new RivTaProfil();
        profil1.setBeskrivning("A_B"); // Ska matcha
        service.save(profil1);

        RivTaProfil profil2 = new RivTaProfil();
        profil2.setBeskrivning("A-B"); // Ska inte matcha
        service.save(profil2);

        RivTaProfil profil3 = new RivTaProfil();
        profil3.setBeskrivning("A B"); // Ska inte matcha
        service.save(profil3);

        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", FilterCondition.CONTAINS, "_"));

        PagedEntityList<?> result = service.getEntityList(0, 100, filters, null, false, false);

        assertEquals(1, result.getSize()); // Endast "A_B" ska matcha. Testar filtret endast matchar förväntat resultat. "_" Inte behandlas som ett wildcard.
        Optional<RivTaProfil> fetchedProfil = service.findById(profil1.getId());
        assertEquals("A_B", fetchedProfil.get().getBeskrivning()); // Testar att faktiskta objektet i databasen är korrekt
    }

    //Tester för att se att specialtecken inte fungerar som wildcard
    @Test
    public void testFilterListMatchesExactUnderscore() {
        RivTaProfil profil1 = new RivTaProfil();
        profil1.setBeskrivning("A_B_C"); // Ska matcha
        service.save(profil1);

        RivTaProfil profil2 = new RivTaProfil();
        profil2.setBeskrivning("ABC"); // Ska inte matcha
        service.save(profil2);

        RivTaProfil profil3 = new RivTaProfil();
        profil3.setBeskrivning("A-C"); // Ska inte matcha
        service.save(profil3);

        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", FilterCondition.CONTAINS, "_"));

        PagedEntityList<?> result = service.getEntityList(0, 100, filters, null, false, false);

        assertEquals(1, result.getSize()); // Endast "A_B_C" ska matcha
        Optional<RivTaProfil> fetchedProfil = service.findById(profil1.getId());
        assertEquals("A_B_C", fetchedProfil.get().getBeskrivning());
    }

    @Test
    public void testFilterListDoesNotMatchWithoutUnderscore() {
        RivTaProfil profil1 = new RivTaProfil();
        profil1.setBeskrivning("ABC");
        service.save(profil1);

        RivTaProfil profil2 = new RivTaProfil();
        profil2.setBeskrivning("A B C");
        service.save(profil2);

        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", FilterCondition.CONTAINS, "_"));

        PagedEntityList<?> result = service.getEntityList(0, 100, filters, null, false, false);

        assertEquals(0, result.getSize()); // Ingen ska matcha
    }


    @Test
    public void testFilterListBeskrivningEquals() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("beskrivning", FilterCondition.EQUALS, "RIV TA BP 2.0-Published"));
        PagedEntityList<RivTaProfil> result = service.getEntityList(0, 100, filters, null, false, false);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testFilterListNamnStarts() {
        List<ListFilter> filters = new ArrayList<>();
        filters.add(new ListFilter("namn", FilterCondition.STARTS_WITH, "RIVTA"));
        PagedEntityList<RivTaProfil> result = service.getEntityList(0, 100, filters, null, false, false);
        assertNotNull(result);
        assertEquals(7, result.getContent().size());
    }

    @Test
    public void testListOffset() {
        List<ListFilter> filters = new ArrayList<>();
        PagedEntityList<RivTaProfil> list = service.getEntityList(5, 5, filters, null, false, false);
        assertEquals(2, list.getContent().size());
        assertEquals(7, list.getTotalElements());
    }

    @Test
    public void testMax() {
        List<ListFilter> filters = new ArrayList<>();
        PagedEntityList<RivTaProfil> list = service.getEntityList(0, 5, filters, null, false, false);
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
