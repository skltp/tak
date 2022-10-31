package se.skltp.tak.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsEmptyCollection;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.util.BestallningsDataValidator;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class BestallningServiceTests {

    @Mock AnropsAdressService anropsAdressService;
    @Mock AnropsBehorighetService anropsBehorighetService;
    @Mock LogiskAdressService logiskAdressService;
    @Mock RivTaProfilService rivTaProfilService;
    @Mock TjanstekomponentService tjanstekomponentService;
    @Mock TjanstekontraktService tjanstekontraktService;
    @Mock VagvalService vagvalService;

    BestallningService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RivTaProfil profil = new RivTaProfil();
        profil.setNamn("RIVTABP21");
        profil.setId(1L);
        profil.setPubVersion("1");
        Mockito.when(rivTaProfilService.getRivTaProfilByNamn("RIVTABP21")).thenReturn(profil);

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        BestallningsDataValidator validator = new BestallningsDataValidator(validatorFactory.getValidator());

        service = new BestallningService(anropsAdressService, anropsBehorighetService, logiskAdressService,
                rivTaProfilService, tjanstekomponentService, tjanstekontraktService, vagvalService, validator);
    }

    @Test
    public void testEmptyStringThrowsIllegalArgumentException() throws Exception {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseAndFormatJson("");
        });
    }

    @Test
    public void testEmptyJsonThrowsIllegalArgumentException() throws Exception {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.parseAndFormatJson("{}");
        });
    }
    @Test
    public void testParseMetadata() throws Exception {
        String input = "{ \"plattform\" : \"SKLTP-TEST\", \"formatVersion\" : 1.0, \"version\" : 1, "
                + "\"bestallningsTidpunkt\" : \"2022-05-22T12:00:01+0000\", "
                + "\"genomforandeTidpunkt\" : \"2022-05-24T12:00:01+0000\","
                + "\"utforare\" : \"TEST\", \"kommentar\" : \"HEJ\" }";

        String formatted = service.parseAndFormatJson(input);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(formatted, Map.class);
        assertEquals("SKLTP-TEST", map.get("plattform"));
        assertEquals(1.0, map.get("formatVersion"));
        assertEquals(1, map.get("version"));
        assertEquals("2022-05-22T12:00:01+0000", map.get("bestallningsTidpunkt"));
        assertEquals("2022-05-24T12:00:01+0000", map.get("genomforandeTidpunkt"));
        assertEquals("TEST", map.get("utforare"));
        assertEquals("HEJ", map.get("kommentar"));
    }

    @Test
    public void testBuildBestallningsData() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertNotNull(data);
        assertThat(data.getBestallningErrors(), IsEmptyCollection.empty());
        assertFalse(data.hasErrors());
    }

    @Test
    public void testBestallningsRapport() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsRapport rapport = service.buildBestallningsData(input, "TEST_USER").getBestallningsRapport();
        assertNotNull(rapport);
        assertEquals(7, rapport.getMetadata().size());
        assertNotNull(rapport.getInkludera());
        assertEquals(1, rapport.getInkludera().get("Logiska adresser").size());
        assertEquals(1, rapport.getInkludera().get("Tjänstekontrakt").size());
        assertEquals(2, rapport.getInkludera().get("Tjänstekomponenter").size());
        assertEquals(1, rapport.getInkludera().get("Anropsbehörigheter").size());
        assertEquals(1, rapport.getInkludera().get("Vägval").size());
        assertEquals(0, rapport.getExkludera().get("Logiska adresser").size());
        assertEquals(0, rapport.getExkludera().get("Tjänstekontrakt").size());
        assertEquals(0, rapport.getExkludera().get("Tjänstekomponenter").size());
        assertEquals(0, rapport.getExkludera().get("Anropsbehörigheter").size());
        assertEquals(0, rapport.getExkludera().get("Vägval").size());

    }
}
