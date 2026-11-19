/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.skltp.tak.web.exception.MasstakningException;

public class MasstakningServiceTest {

    static final String NS1 = "urn:riv:clinicalprocess:healthcond:actoutcome:GetLaboratoryOrderOutcomeResponder:4";
    static final String NS2 = "urn:riv:crm:requeststatus:GetRequestActivitiesResponder:1";

    private MasstakningService service;

    @BeforeEach
    void setUp() {
        service = new MasstakningService();
        service.setClock(Clock.fixed(Instant.parse("2026-07-01T10:15:30Z"), ZoneId.of("Europe/Stockholm")));
    }

    /** Creates a workbook following the mass-TAKning template. Data starts on row 3. */
    static byte[] createWorkbook(String[][] contracts, String[][] producers, String[][] logicalAddresses,
            String[][] consumers) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            addSheet(wb, "Tjänstekontrakt", contracts);
            addSheet(wb, "Producenter", producers);
            addSheet(wb, "Logiska adresser", logicalAddresses);
            if (consumers != null) {
                addSheet(wb, "Konsumenter", consumers);
            }
            wb.write(out);
            return out.toByteArray();
        }
    }

    private static void addSheet(XSSFWorkbook wb, String name, String[][] rows) {
        Sheet sheet = wb.createSheet(name);
        sheet.createRow(0).createCell(0).setCellValue("Rubrik");
        sheet.createRow(1).createCell(0).setCellValue("Kolumnrubrik");
        for (int r = 0; r < rows.length; r++) {
            Row row = sheet.createRow(r + 2);
            for (int c = 0; c < rows[r].length; c++) {
                if (rows[r][c] != null) {
                    row.createCell(c).setCellValue(rows[r][c]);
                }
            }
        }
    }

    public static byte[] defaultWorkbook() throws IOException {
        return createWorkbook(
                new String[][] {
                        { "Domän", NS1, "Kontrakt 1", "https://producer/ns1" },
                        { "Domän", NS2, "Kontrakt 2", "https://producer/ns2" } },
                new String[][] { { "SE123-PROD", "Producent" } },
                new String[][] { { " SE123-LA1 ", " Adress 1 " }, { "SE123-LA2", "Adress 2" } },
                new String[][] { { "SE123-CON", "Konsument" } });
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> list(Map<String, Object> section, String key) {
        return (List<Map<String, Object>>) section.get(key);
    }

    @Test
    @SuppressWarnings("unchecked")
    void takaCreatesIncludeSection() throws IOException {
        Map<String, Object> result = service.createTakOrder(defaultWorkbook(), "NTJP-PROD", "TAKA");

        assertEquals(List.of("plattform", "formatVersion", "version", "bestallningsTidpunkt", "utforare",
                "genomforandeTidpunkt", "inkludera"), List.copyOf(result.keySet()));
        assertEquals("NTJP-PROD", result.get("plattform"));
        assertEquals("1.0", result.get("formatVersion"));
        assertEquals("1", result.get("version"));
        assertEquals("2026-07-01T12:15:30+0100", result.get("bestallningsTidpunkt"));
        assertEquals("2026-07-01T12:15:30+0100", result.get("genomforandeTidpunkt"));
        assertEquals(MasstakningService.EXECUTOR_INERA, result.get("utforare"));

        Map<String, Object> section = (Map<String, Object>) result.get("inkludera");
        assertEquals(List.of("tjanstekomponenter", "tjanstekontrakt", "logiskadresser", "anropsbehorigheter", "vagval"),
                List.copyOf(section.keySet()));

        assertEquals(List.of(
                Map.of("hsaId", "SE123-PROD", "beskrivning", "Producent"),
                Map.of("hsaId", "SE123-CON", "beskrivning", "Konsument")), list(section, "tjanstekomponenter"));
        assertEquals(List.of(
                Map.of("namnrymd", NS1, "majorVersion", 4, "beskrivning", "Kontrakt 1"),
                Map.of("namnrymd", NS2, "majorVersion", 1, "beskrivning", "Kontrakt 2")), list(section, "tjanstekontrakt"));
        assertEquals(List.of(
                Map.of("hsaId", "SE123-LA1", "beskrivning", "Adress 1"),
                Map.of("hsaId", "SE123-LA2", "beskrivning", "Adress 2")), list(section, "logiskadresser"));

        List<Map<String, Object>> vagval = list(section, "vagval");
        assertEquals(4, vagval.size());
        assertEquals(Map.of("tjanstekomponent", "SE123-PROD", "logiskAdress", "SE123-LA1", "tjanstekontrakt", NS1,
                "rivtaprofil", "RIVTABP21", "adress", "https://producer/ns1"), vagval.get(0));
        assertEquals(List.of("tjanstekomponent", "logiskAdress", "tjanstekontrakt", "rivtaprofil", "adress"),
                List.copyOf(vagval.get(0).keySet()));
        assertEquals("SE123-LA2", vagval.get(3).get("logiskAdress"));
        assertEquals(NS2, vagval.get(3).get("tjanstekontrakt"));

        List<Map<String, Object>> behorigheter = list(section, "anropsbehorigheter");
        assertEquals(4, behorigheter.size());
        assertEquals(Map.of("tjanstekonsument", "SE123-CON", "logiskAdress", "SE123-LA1", "tjanstekontrakt", NS1),
                behorigheter.get(0));
    }

    @Test
    void avTakaCreatesExcludeSectionAndRtpExecutor() throws IOException {
        Map<String, Object> result = service.createTakOrder(defaultWorkbook(), "SLL-QA", "AV-TAKA");

        assertTrue(result.containsKey("exkludera"));
        assertFalse(result.containsKey("inkludera"));
        assertEquals(MasstakningService.EXECUTOR_RTP, result.get("utforare"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void duplicatesAreRemovedAndBlankRowsSkipped() throws IOException {
        byte[] file = createWorkbook(
                new String[][] { { "D", NS1, "K", "https://a" }, { null, null, null, null }, { "D", NS1, "K", "https://b" } },
                new String[][] { { "SE123-PROD", "Producent" } },
                new String[][] { { "SE123-LA1", "A" }, { "SE123-LA1", "A" } },
                new String[][] {});

        Map<String, Object> section = (Map<String, Object>) service.createTakOrder(file, "NTJP-QA", "TAKA").get("inkludera");

        assertEquals(1, list(section, "tjanstekontrakt").size());
        assertEquals(1, list(section, "logiskadresser").size());
        assertEquals(1, list(section, "tjanstekomponenter").size());
        assertTrue(list(section, "anropsbehorigheter").isEmpty());
        List<Map<String, Object>> vagval = list(section, "vagval");
        assertEquals(1, vagval.size());
        // The URL from the last matching contract row is used
        assertEquals("https://b", vagval.get(0).get("adress"));
    }

    @Test
    void moreThanOneProducerIsRejected() throws IOException {
        byte[] file = createWorkbook(
                new String[][] { { "D", NS1, "K", "https://a" } },
                new String[][] { { "SE1", "P1" }, { "SE2", "P2" } },
                new String[][] { { "SE123-LA1", "A" } },
                new String[][] {});

        MasstakningException e = assertThrows(MasstakningException.class,
                () -> service.createTakOrder(file, "NTJP-PROD", "TAKA"));
        assertEquals(MasstakningService.MSG_ONLY_ONE_PRODUCER, e.getMessage());
    }

    @Test
    void missingUrlIsRejected() throws IOException {
        byte[] file = createWorkbook(
                new String[][] { { "D", NS1, "K", null } },
                new String[][] { { "SE1", "P1" } },
                new String[][] { { "SE123-LA1", "A" } },
                new String[][] {});

        MasstakningException e = assertThrows(MasstakningException.class,
                () -> service.createTakOrder(file, "NTJP-PROD", "TAKA"));
        assertTrue(e.getMessage().startsWith("Hittade inte URL för " + NS1));
    }

    @Test
    void missingSheetIsRejected() throws IOException {
        byte[] file = createWorkbook(
                new String[][] { { "D", NS1, "K", "https://a" } },
                new String[][] { { "SE1", "P1" } },
                new String[][] { { "SE123-LA1", "A" } },
                null);

        MasstakningException e = assertThrows(MasstakningException.class,
                () -> service.createTakOrder(file, "NTJP-PROD", "TAKA"));
        assertTrue(e.getMessage().contains("Konsumenter"));
    }

    @Test
    void notAnExcelFileIsRejected() {
        MasstakningException e = assertThrows(MasstakningException.class,
                () -> service.createTakOrder("not excel".getBytes(), "NTJP-PROD", "TAKA"));
        assertEquals(MasstakningService.MSG_INVALID_FILE, e.getMessage());
    }

    @Test
    void unknownPlattformAndActionAreRejected() throws IOException {
        byte[] file = defaultWorkbook();
        assertThrows(MasstakningException.class, () -> service.createTakOrder(file, "FOO", "TAKA"));
        assertThrows(MasstakningException.class, () -> service.createTakOrder(file, "NTJP-PROD", "FOO"));
    }
}
