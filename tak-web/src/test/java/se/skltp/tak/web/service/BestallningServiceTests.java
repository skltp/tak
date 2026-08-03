/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
class BestallningServiceTests {
    @Autowired AnropsBehorighetRepository anropsBehorighetRepository;
    @Autowired LogiskAdressRepository logiskAdressRepository;
    @Autowired TjanstekomponentRepository tjanstekomponentRepository;
    @Autowired TjanstekontraktRepository tjanstekontraktRepository;
    @Autowired VagvalRepository vagvalRepository;

    @MockitoBean ConfigurationService configurationMock;
    @Autowired BestallningService service;

    @Test
    void testEmptyStringThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.parseAndFormatJson("");
        });
    }

    @Test
    void testEmptyJsonThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.parseAndFormatJson("{}");
        });
    }
    @Test
    void testParseMetadata() throws Exception {
        String input = "{ \"plattform\" : \"SKLTP-TEST\", \"formatVersion\" : 1.0, \"version\" : 1, "
                + "\"bestallningsTidpunkt\" : \"2022-05-22T12:00:01+0000\", "
                + "\"genomforandeTidpunkt\" : \"2022-05-24T12:00:01+0000\","
                + "\"utforare\" : \"TEST\", \"kommentar\" : \"HEJ\" }";

        String formatted = service.parseAndFormatJson(input);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(formatted, Map.class);
        assertEquals("SKLTP-TEST", map.get("plattform"));
        assertEquals(1.0, map.get("formatVersion"));
        assertEquals(1, map.get("version"));
        assertEquals("2022-05-22T12:00:01+0000", map.get("bestallningsTidpunkt"));
        assertEquals("2022-05-24", map.get("genomforandeTidpunkt"));
        assertEquals("TEST", map.get("utforare"));
        assertEquals("HEJ", map.get("kommentar"));
    }

    @Test
    void testBuildBestallningsData() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertNotNull(data);
        assertThat(data.getBestallningErrors(), IsEmptyCollection.empty());
        assertFalse(data.hasErrors());
    }
    @Test
    void testBestallningsDataHashes() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertNotNull(data);
        assertThat(data.getBestallningErrors(), IsEmptyCollection.empty());
        assertFalse(data.hasErrors());

        int hashOrig = data.hashCode();

        // Change address and make sure hash changes, change it back and make sure that the hash is back to original again
        String addrOrig = data.getBestallning().getInkludera().getVagval().get(0).getAdress();
        data.getBestallning().getInkludera().getVagval().get(0).setAdress("none");
        assertNotEquals(hashOrig, data.hashCode(), "Hash should not be equal to original when address is changed");
        data.getBestallning().getInkludera().getVagval().get(0).setAdress(addrOrig);
        assertEquals(hashOrig, data.hashCode());

        // Change rivtaprofil and make sure hash changes, change it back and make sure that the hash is back to original again
        String rivtapOrig = data.getBestallning().getInkludera().getVagval().get(0).getRivtaprofil();
        data.getBestallning().getInkludera().getVagval().get(0).setRivtaprofil("rivrav");
        assertNotEquals(hashOrig, data.hashCode(), "Hash should not be equal to original when rivtaprofil is changed");
        data.getBestallning().getInkludera().getVagval().get(0).setRivtaprofil(rivtapOrig);
        assertEquals(hashOrig, data.hashCode());

        // Change tjanstekomponent and make sure hash changes, change it back and make sure that the hash is back to original again
        data.getBestallning().getInkludera().getTjanstekomponenter().get(0).setBeskrivning("min beskrivning"); // note that beskrivning should not affect the hash
        String hsaIdOrig = data.getBestallning().getInkludera().getTjanstekomponenter().get(0).getHsaId();
        data.getBestallning().getInkludera().getTjanstekomponenter().get(0).setHsaId("MY_HSA000");
        assertNotEquals(hashOrig, data.hashCode(), "Hash should not be equal to original when HSA-id is changed");
        data.getBestallning().getInkludera().getTjanstekomponenter().get(0).setHsaId(hsaIdOrig);
        assertEquals(hashOrig, data.hashCode());




    }

    @Test
    void testBuildBestallningsDataCheckPlatform() throws Exception {
        when(configurationMock.getPlatform()).thenReturn("SKLTP-TEST");
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertNotNull(data);
        assertThat(data.getBestallningErrors(), IsEmptyCollection.empty());
        assertFalse(data.hasErrors());
    }

    @Test
    void testBuildBestallningsDataWrongPlatform() throws Exception {
        when(configurationMock.getPlatform()).thenReturn("ANOTHER-PLATFORM");
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        assertEquals(1, data.getBestallningErrors().size());
    }

    @Test
    void testBuildBestallningsDataExkludera() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera_deaktivera.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertNotNull(data);
        assertThat(data.getBestallningErrors(), IsEmptyCollection.empty());
        assertFalse(data.hasErrors());
    }

    @ParameterizedTest
    @CsvSource({
        "bestallning-test-missing-url.json, 1",
        "bestallning-test-empty-fields.json, 3",
        "bestallning-test-duplicates.json, 4",
        "bestallning-test-missing-relations.json, 7"
    })
    void testBuildBestallningsDataWithErrors(String fileName, int expectedErrorCount) throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/" + fileName)));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        assertEquals(expectedErrorCount, data.getBestallningErrors().size());
    }

    @Test
    void testBuildBestallningsDataWithEmptyAddress() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-empty-address.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertTrue(data.hasErrors());
        Set<String> errors = data.getBestallningErrors();
        assertEquals(1, errors.size());
        assertTrue(errors.toArray()[0].toString().contains("Adress får inte vara tom"));
    }


    @Test
    void testBuildBestallningsDataExkluderaNoErrorIfMissing() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera-missing.json")));

        BestallningsData data = service.buildBestallningsData(input, "TEST_USER");
        assertFalse(data.hasErrors());
    }

    @Test
    void testBuildBestallningsDataExkluderaDeletePlainObjekts() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera-missing.json")));

        BestallningsRapport rapport = service.buildBestallningsData(input, "TEST_USER").getBestallningsRapport();
        assertNull(rapport.getExkludera().get("Logiska adresser"));
        assertNull(rapport.getExkludera().get("Tjänstekontrakt"));
        assertNull(rapport.getExkludera().get("Tjänstekomponenter"));
        assertNotNull(rapport.getExkludera().get("Anropsbehörigheter"));
        assertNotNull(rapport.getExkludera().get("Vägval"));
    }

    @Test
    void testBestallningsRapport() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-simple.json")));

        BestallningsRapport rapport = service.buildBestallningsData(input, "TEST_USER").getBestallningsRapport();
        assertNotNull(rapport);
        assertEquals(7, rapport.getRapportHuvud().size());
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
    void testBestallningsRapportForUpdatedVagval() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-update-vagval1.json")));

        BestallningsRapport rapport = service.buildBestallningsData(input, "TEST_USER").getBestallningsRapport();
        assertNotNull(rapport);
        assertEquals(7, rapport.getRapportHuvud().size());
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
    void testExecuteBestallningsData() throws Exception {
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
    void testDoNotSaveDeletedAfterBuildBestallningsData() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera_delete.json")));
        service.buildBestallningsData(input, "TEST_USER");

        Anropsbehorighet ab2 = anropsBehorighetRepository.findById(10L).get();
        assertFalse(ab2.getDeleted(), "Anropsbehorighet was deleted in DB after buildBestallningsData");

        Vagval vv2 = vagvalRepository.findById(12L).get();
        assertFalse(vv2.getDeleted(), "VV was changed in DB deleted buildBestallningsData");
    }

    @Test
    void testDoNotSaveDeakvideradAfterBuildBestallningsData() throws Exception {
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
    void testDoNotSaveChangedAfterBuildBestallningsData() throws Exception {
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
    void testDoNotSaveChangedAfterBuildBestallningsData1() throws Exception {
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
    void testDoNotSaveChangedAfterBuildBestallningsData2() throws Exception {
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
    void testDoNotSaveChangedAfterBuildBestallningsData_AnnanAnropsAdress() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-update-vagval3.json")));
        service.buildBestallningsData(input, "TEST_USER");

        Vagval oldVagval = vagvalRepository.findById(12L).get();
        AnropsAdress oldAnropsAddress = oldVagval.getAnropsAdress();

        assertFalse(oldVagval.getDeleted(), "Vägval was deleted in DB after buildBestallningsData");
        assertFalse(oldAnropsAddress.getDeleted(), "Old anropsAddress was deleted in DB after buildBestallningsData");
    }

    @Test
    void testExecuteBestallningsDataExkludera() throws Exception {
        String input = new String(Files.readAllBytes(Paths.get("src/test/resources/bestallning-test-exkludera_deaktivera.json")));

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
