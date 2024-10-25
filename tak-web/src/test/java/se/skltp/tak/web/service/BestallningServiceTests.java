package se.skltp.tak.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.repository.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BestallningServiceTests {
    @Autowired AnropsBehorighetRepository anropsBehorighetRepository;
    @Autowired LogiskAdressRepository logiskAdressRepository;
    @Autowired TjanstekomponentRepository tjanstekomponentRepository;
    @Autowired TjanstekontraktRepository tjanstekontraktRepository;
    @Autowired VagvalRepository vagvalRepository;

    @MockBean ConfigurationService configurationMock;
    @Autowired BestallningService service;

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
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera_deaktivera.json")));

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
    public void testBuildBestallningsDataWithEmptyFields() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-empty-fields.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        assertEquals(3, data.getBestallningErrors().size());
    }

    @Test
    public void testBuildBestallningsDataWithEmptyAddress() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-empty-address.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        Set<String> errors = data.getBestallningErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.stream().toArray()[0].toString().contains("Adress får inte vara tom"));
    }

    @Test
    public void testBuildBestallningsDataDuplicates() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-duplicates.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        assertEquals(4, data.getBestallningErrors().size());
    }

    @Test
    public void testBuildBestallningsDataMissingRelations() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-missing-relations.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        assertEquals(7, data.getBestallningErrors().size());
    }

    @Test
    public void testBuildBestallningsDataExkluderaNoErrorIfMissing() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera-missing.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertFalse(data.hasErrors());
    }

    @Test
    public void testBuildBestallningsDataExkluderaDeletePlainObjekts() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera-missing.json")));

        BestallningsRapport rapport = service.buildBestallningsData(input, "TEST_USER").getBestallningsRapport();
        assertNull(rapport.getExkludera().get("Logiska adresser"));
        assertNull(rapport.getExkludera().get("Tjänstekontrakt"));
        assertNull(rapport.getExkludera().get("Tjänstekomponenter"));
        assertNotNull(rapport.getExkludera().get("Anropsbehörigheter"));
        assertNotNull(rapport.getExkludera().get("Vägval"));
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
        assertNull(rapport.getExkludera().get("Logiska adresser"));
        assertNull( rapport.getExkludera().get("Tjänstekontrakt"));
        assertNull(rapport.getExkludera().get("Tjänstekomponenter"));
        assertNull(rapport.getExkludera().get("Anropsbehörigheter"));
        assertNull(rapport.getExkludera().get("Vägval"));
        assertTrue(rapport.toString().contains("urn:riv:clinicalprocess:activity:actions:GetActivitiesResponder:2"));
        assertTrue(rapport.toString().contains("TEST-001"));
        assertTrue(rapport.toString().contains("PROD-001"));
        assertTrue(rapport.toString().contains("KONS-001"));
    }

    @Test
    public void testBestallningsRapportForUpdatedVagval() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-update-vagval1.json")));

        BestallningsRapport rapport = service.buildBestallningsData(input, "TEST_USER").getBestallningsRapport();
        assertNotNull(rapport);
        assertEquals(7, rapport.getMetadata().size());
        assertNotNull(rapport.getInkludera());
        assertNull(rapport.getInkludera().get("Logiska adresser"));
        assertNull(rapport.getInkludera().get("Tjänstekontrakt"));
        assertNull(rapport.getInkludera().get("Tjänstekomponenter"));
        assertEquals(1, rapport.getInkludera().get("Anropsbehörigheter").size());
        assertEquals(2, rapport.getInkludera().get("Vägval").size());
        assertNull(rapport.getExkludera().get("Logiska adresser"));
        assertNull(rapport.getExkludera().get("Tjänstekontrakt"));
        assertNull(rapport.getExkludera().get("Tjänstekomponenter"));
        assertNull(rapport.getExkludera().get("Anropsbehörigheter"));
        assertNull(rapport.getExkludera().get("Vägval"));
        assertTrue(rapport.toString().contains("urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1"));
        assertTrue(rapport.toString().contains("HSA-VKK123"));
        assertTrue(rapport.toString().contains("SCHEDULR"));
    }

    @Test
    public void testExecuteBestallningsData() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));
        String namnrymd = "urn:riv:clinicalprocess:activity:actions:GetActivitiesResponder:2";

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        service.execute(data, "TEST_USER");

        Tjanstekontrakt tk = tjanstekontraktRepository.findFirstByNamnrymdAndDeleted(namnrymd, false);
        LogiskAdress la = logiskAdressRepository.findFirstByHsaIdAndDeleted("TEST-001", false);
        Tjanstekomponent prod = tjanstekomponentRepository.findFirstByHsaIdAndDeleted("PROD-001", false);
        Tjanstekomponent kons = tjanstekomponentRepository.findFirstByHsaIdAndDeleted("KONS-001", false);
        List<Anropsbehorighet> ab = anropsBehorighetRepository.findMatchingNonDeleted("TEST-001", "KONS-001", namnrymd);
        List<Vagval> vv = vagvalRepository.findMatchingNonDeleted("TEST-001", namnrymd);
        assertNotNull(tk);
        assertNotNull(la);
        assertNotNull(prod);
        assertNotNull(kons);
        assertNotNull(ab);
        assertEquals(1, ab.size());
        assertNotNull(vv);
        assertEquals(1, vv.size());
    }

    @Test
    public void testDoNotSaveDeletedAfterBuildBestallningsData() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera_delete.json")));
        service.buildBestallningsData(input, "TEST_USER");

        Anropsbehorighet ab2 = anropsBehorighetRepository.findById(10L).get();
        assertFalse(ab2.getDeleted(), "Anropsbehorighet was deleted in DB after buildBestallningsData");

        Vagval vv2 = vagvalRepository.findById(12L).get();
        assertFalse(vv2.getDeleted(), "VV was changed in DB deleted buildBestallningsData");
    }

    @Test
    public void testDoNotSaveDeakvideradAfterBuildBestallningsData() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera_deaktivera.json")));

        Anropsbehorighet ab = anropsBehorighetRepository.findById(7L).get();
        Date originalABTomTidpunkt = ab.getTomTidpunkt();

        Vagval vv = vagvalRepository.findById(6L).get();
        Date originalVVTomTidpunkt = vv.getTomTidpunkt();

        service.buildBestallningsData(input, "TEST_USER");

        Anropsbehorighet ab2 = anropsBehorighetRepository.findById(7L).get();
        assertEquals(originalABTomTidpunkt, ab2.getTomTidpunkt(), "Anropsbehorighet was changed in DB after buildBestallningsData");

        Vagval vv2 = vagvalRepository.findById(6L).get();
        assertEquals(originalVVTomTidpunkt, vv2.getTomTidpunkt(), "VV was changed in DB after buildBestallningsData");
    }

    @Test
    public void testDoNotSaveChangedAfterBuildBestallningsData() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-update-vagval.json")));

        Anropsbehorighet ab = anropsBehorighetRepository.findById(7L).get();
        Date originalABTomTidpunkt = ab.getTomTidpunkt();

        Vagval vv = vagvalRepository.findById(6L).get();
        Date originalVVTomTidpunkt = vv.getTomTidpunkt();

        service.buildBestallningsData(input, "TEST_USER");

        Anropsbehorighet ab2 = anropsBehorighetRepository.findById(7L).get();
        assertEquals(originalABTomTidpunkt, ab2.getTomTidpunkt(), "Anropsbehorighet was changed in DB after buildBestallningsData");

        Vagval vv2 = vagvalRepository.findById(6L).get();
        assertEquals(originalVVTomTidpunkt, vv2.getTomTidpunkt(), "VV was changed in DB after buildBestallningsData");
    }

    @Test
    public void testDoNotSaveChangedAfterBuildBestallningsData1() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-update-vagval1.json")));

        Anropsbehorighet ab = anropsBehorighetRepository.findById(1L).get();
        Date originalABTomTidpunkt = ab.getTomTidpunkt();

        Vagval vv = vagvalRepository.findById(1L).get();
        Date originalVVTomTidpunkt = vv.getTomTidpunkt();

        service.buildBestallningsData(input, "TEST_USER");

        Anropsbehorighet ab2 = anropsBehorighetRepository.findById(1L).get();
        assertEquals(originalABTomTidpunkt, ab2.getTomTidpunkt(), "Anropsbehorighet was changed in DB after buildBestallningsData");

        Vagval vv2 = vagvalRepository.findById(1L).get();
        assertEquals(originalVVTomTidpunkt, vv2.getTomTidpunkt(), "VV was changed in DB after buildBestallningsData");
    }

    @Test
    public void testDoNotSaveChangedAfterBuildBestallningsData2() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-update-vagval2.json")));

        Anropsbehorighet ab = anropsBehorighetRepository.findById(10L).get();
        Date originalABFromTidpunkt = ab.getFromTidpunkt();
        Date originalABTomTidpunkt = ab.getTomTidpunkt();

        Vagval vv = vagvalRepository.findById(12L).get();
        Date originalVVFromTidpunkt = vv.getFromTidpunkt();
        Date originalVVTomTidpunkt = vv.getTomTidpunkt();

        service.buildBestallningsData(input, "TEST_USER");

        Anropsbehorighet ab2 = anropsBehorighetRepository.findById(10L).get();
        assertEquals(originalABFromTidpunkt, ab2.getFromTidpunkt(), "Anropsbehorighet was changed in DB after buildBestallningsData");
        assertEquals(originalABTomTidpunkt, ab2.getTomTidpunkt(), "Anropsbehorighet was changed in DB after buildBestallningsData");

        Vagval vv2 = vagvalRepository.findById(12L).get();
        assertEquals(originalVVFromTidpunkt, vv2.getFromTidpunkt(), "VV was changed in DB after buildBestallningsData");
        assertEquals(originalVVTomTidpunkt, vv2.getTomTidpunkt(), "VV was changed in DB after buildBestallningsData");
    }

    @Test
    public void testDoNotSaveChangedAfterBuildBestallningsData_AnnanAnropsAdress() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-update-vagval3.json")));
        service.buildBestallningsData(input, "TEST_USER");

        Vagval oldVagval = vagvalRepository.findById(12L).get();
        AnropsAdress oldAnropsAddress = oldVagval.getAnropsAdress();

        assertFalse(oldVagval.getDeleted(), "Vägval was deleted in DB after buildBestallningsData");
        assertFalse(oldAnropsAddress.getDeleted(), "Old anropsAddress was deleted in DB after buildBestallningsData");
    }

    @Test
    public void testExecuteBestallningsDataExkludera() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera_deaktivera.json")));
        String namnrymd = "urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1";

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        service.execute(data, "TEST_USER");

        Optional<Anropsbehorighet> ab = anropsBehorighetRepository.findById(7L);
        assertTrue(ab.isPresent());
        assertFalse(ab.get().getDeleted());
        assertTrue(ab.get().getTomTidpunkt().before(new Date()));

        Optional<Vagval> vv = vagvalRepository.findById(6L);
        assertTrue(vv.isPresent());
        assertFalse(vv.get().getDeleted());
        assertTrue(vv.get().getTomTidpunkt().before(new Date()));
    }



}
