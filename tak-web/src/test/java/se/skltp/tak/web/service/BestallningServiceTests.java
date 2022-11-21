package se.skltp.tak.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import se.skltp.tak.core.entity.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BestallningServiceTests {

    @Mock AnropsAdressService anropsAdressMock;
    @Mock AnropsBehorighetService anropsBehorighetMock;
    @Mock LogiskAdressService logiskAdressMock;
    @Mock RivTaProfilService rivTaProfilMock;
    @Mock TjanstekomponentService tjanstekomponentMock;
    @Mock TjanstekontraktService tjanstekontraktMock;
    @Mock VagvalService vagvalMock;
    @Mock ConfigurationService configurationMock;

    BestallningService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RivTaProfil profil = new RivTaProfil();
        profil.setNamn("RIVTABP21");
        profil.setId(1L);
        profil.setPubVersion("1");
        Mockito.when(rivTaProfilMock.getRivTaProfilByNamn("RIVTABP21")).thenReturn(profil);

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        BestallningsDataValidator validator = new BestallningsDataValidator(validatorFactory.getValidator());

        service = new BestallningService(anropsAdressMock, anropsBehorighetMock, logiskAdressMock,
                rivTaProfilMock, tjanstekomponentMock, tjanstekontraktMock, vagvalMock, configurationMock, validator);
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
    public void testBuildBestallningsDataCheckPlatform() throws Exception {
        Mockito.when(configurationMock.getPlatform()).thenReturn("SKLTP-TEST");
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertNotNull(data);
        assertThat(data.getBestallningErrors(), IsEmptyCollection.empty());
        assertFalse(data.hasErrors());
    }

    @Test
    public void testBuildBestallningsDataWrongPlatform() throws Exception {
        Mockito.when(configurationMock.getPlatform()).thenReturn("ANOTHER-PLATFORM");
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        assertEquals(1, data.getBestallningErrors().size());
    }

    @Test
    public void testBuildBestallningsDataExkludera() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertNotNull(data);
        assertThat(data.getBestallningErrors(), IsEmptyCollection.empty());
        assertFalse(data.hasErrors());
    }

    @Test
    public void testBuildBestallningsDataMissingUrl() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-missing-url.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        assertEquals(1, data.getBestallningErrors().size());
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
        assertTrue(rapport.toString().contains("urn:riv:clinicalprocess:activity:actions:GetActivitiesResponder:2"));
        assertTrue(rapport.toString().contains("TEST-001"));
        assertTrue(rapport.toString().contains("PROD-001"));
        assertTrue(rapport.toString().contains("KONS-001"));
    }

    @Test
    public void testExecuteBestallningsData() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        service.execute(data, "TEST_USER");

        verify(tjanstekontraktMock, times(1)).update(any(Tjanstekontrakt.class), eq("TEST_USER"));
        verify(logiskAdressMock, times(1)).update(any(LogiskAdress.class), eq("TEST_USER"));
        verify(tjanstekomponentMock, times(2)).update(any(Tjanstekomponent.class), eq("TEST_USER"));
        verify(anropsBehorighetMock, times(1)).update(any(Anropsbehorighet.class), eq("TEST_USER"));
        verify(anropsAdressMock, times(1)).update(any(AnropsAdress.class), eq("TEST_USER"));

    }
}
