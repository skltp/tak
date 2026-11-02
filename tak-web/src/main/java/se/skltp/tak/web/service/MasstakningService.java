/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import java.io.ByteArrayInputStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.skltp.tak.web.exception.MasstakningException;

/**
 * Java port of the MkRoutingFromExcel.py / BsJson.py scripts used for mass-TAKning.
 * <p>
 * The Excel file must follow the mass-TAKning template with the sheets "Tjänstekontrakt" (A:D),
 * "Producenter" (A:B), "Logiska adresser" (A:B) and "Konsumenter" (A:B). Data starts on row 3.
 */
@Service
public class MasstakningService {

    private static final Logger log = LoggerFactory.getLogger(MasstakningService.class);

    public static final String ACTION_TAKA = "TAKA";
    public static final String ACTION_AVTAKA = "AV-TAKA";
    public static final List<String> PLATTFORMAR = List.of("NTJP-PROD", "NTJP-QA", "SLL-PROD", "SLL-QA");

    static final String SHEET_CONTRACTS = "Tjänstekontrakt";
    static final String SHEET_PRODUCERS = "Producenter";
    static final String SHEET_LOGICAL_ADDRESSES = "Logiska adresser";
    static final String SHEET_CONSUMERS = "Konsumenter";

    static final String EXECUTOR_INERA = "Inera - Forvaltningsobjekt Nationella tjänsteplattformen";
    static final String EXECUTOR_RTP = "Region Stockholm - Forvaltningsobjekt RTP";
    static final String RIVTA_PROFILE = "RIVTABP21";

    static final String MSG_INVALID_FILE =
            "Ett fel upptäcktes i excel-filen. Kontrollera att excel-filen följer mass-TAKnings mallen.";
    static final String MSG_ONLY_ONE_PRODUCER = "Mass-TAKning stöds endast gentemot 1 producent.";

    private static final int FIRST_DATA_ROW = 2;
    // The time zone offset is intentionally hardcoded to +0100, the same as in the original Python script
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0100'");

    private Clock clock = Clock.system(ZoneId.of("Europe/Stockholm"));

    void setClock(Clock clock) {
        this.clock = clock;
    }

    /**
     * Create a TakOrder (BS JSON) from a mass-TAKning Excel file.
     *
     * @param excelFile the .xlsx file content
     * @param plattform the target TP, e.g. NTJP-PROD
     * @param action    TAKA or AV-TAKA
     * @return the TakOrder as an ordered map
     */
    public Map<String, Object> createTakOrder(byte[] excelFile, String plattform, String action) {
        if (!PLATTFORMAR.contains(plattform)) {
            throw new MasstakningException("Okänd TP miljö: " + plattform);
        }
        if (!ACTION_TAKA.equals(action) && !ACTION_AVTAKA.equals(action)) {
            throw new MasstakningException("Okänd åtgärd: " + action);
        }
        if (excelFile == null || excelFile.length == 0) {
            throw new MasstakningException("Ingen excel-fil har angetts.");
        }

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelFile))) {
            Map<String, Object> section = createSection(new SheetReader(workbook));

            Map<String, Object> takOrder = createHeader(plattform);
            takOrder.put(ACTION_TAKA.equals(action) ? "inkludera" : "exkludera", section);
            return takOrder;
        } catch (MasstakningException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Could not read mass-TAKning excel file", e);
            throw new MasstakningException(MSG_INVALID_FILE, e);
        }
    }

    private Map<String, Object> createHeader(String plattform) {
        String now = LocalDateTime.now(clock).format(TIMESTAMP_FORMAT);
        String executor = plattform.startsWith("SLL-") ? EXECUTOR_RTP : EXECUTOR_INERA;

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("plattform", plattform);
        header.put("formatVersion", "1.0");
        header.put("version", "1");
        header.put("bestallningsTidpunkt", now);
        header.put("utforare", executor);
        header.put("genomforandeTidpunkt", now);
        return header;
    }

    private Map<String, Object> createSection(SheetReader reader) {
        Set<Map<String, Object>> components = new LinkedHashSet<>();
        Set<Map<String, Object>> contracts = new LinkedHashSet<>();
        Set<Map<String, Object>> logicalAddresses = new LinkedHashSet<>();
        Set<Map<String, Object>> authorizations = new LinkedHashSet<>();
        Set<Map<String, Object>> routings = new LinkedHashSet<>();

        // Service contracts: B = namespace, C = description, D = URL
        List<String[]> contractRows = reader.read(SHEET_CONTRACTS, 4);
        // The URL is taken from the last row with a matching namespace (as in the Python script)
        Map<String, String> urlByNamespace = new LinkedHashMap<>();
        for (String[] row : contractRows) {
            String namespace = required(row[1], SHEET_CONTRACTS, "namnrymd");
            urlByNamespace.put(namespace, row[3]);
            Map<String, Object> contract = new LinkedHashMap<>();
            contract.put("namnrymd", namespace);
            contract.put("majorVersion", majorVersion(namespace));
            contract.put("beskrivning", nullToEmpty(row[2]));
            contracts.add(contract);
        }

        // Producers: A = HSA-id, B = description. Only one producer is supported.
        List<String[]> producerRows = reader.read(SHEET_PRODUCERS, 2);
        if (producerRows.size() != 1) {
            throw new MasstakningException(MSG_ONLY_ONE_PRODUCER);
        }
        String producerHsaId = required(producerRows.get(0)[0], SHEET_PRODUCERS, "HSA-id");
        components.add(component(producerHsaId, producerRows.get(0)[1]));

        // Logical addresses: A = HSA-id, B = description. Add a routing for each contract.
        List<String[]> laRows = reader.read(SHEET_LOGICAL_ADDRESSES, 2);
        List<String> laHsaIds = new ArrayList<>();
        for (String[] row : laRows) {
            String laHsaId = required(row[0], SHEET_LOGICAL_ADDRESSES, "HSA-id").strip();
            laHsaIds.add(laHsaId);

            Map<String, Object> logicalAddress = new LinkedHashMap<>();
            logicalAddress.put("hsaId", laHsaId);
            logicalAddress.put("beskrivning", nullToEmpty(row[1]).strip());
            logicalAddresses.add(logicalAddress);

            for (Map<String, Object> contract : contracts) {
                String namespace = (String) contract.get("namnrymd");
                String url = urlByNamespace.get(namespace);
                if (url == null) {
                    throw new MasstakningException(
                            "Hittade inte URL för " + namespace + ". Var god kontrollera att excel-filen är rätt.");
                }

                Map<String, Object> routing = new LinkedHashMap<>();
                routing.put("tjanstekomponent", producerHsaId);
                routing.put("logiskAdress", laHsaId);
                routing.put("tjanstekontrakt", namespace);
                routing.put("rivtaprofil", RIVTA_PROFILE);
                if (!url.isEmpty()) {
                    routing.put("adress", url);
                }
                routings.add(routing);
            }
        }

        // Consumers: A = HSA-id, B = description. Add an authorization for each logical address and contract.
        for (String[] row : reader.read(SHEET_CONSUMERS, 2)) {
            String consumerHsaId = required(row[0], SHEET_CONSUMERS, "HSA-id");
            components.add(component(consumerHsaId, row[1]));

            for (String laHsaId : laHsaIds) {
                for (String[] contractRow : contractRows) {
                    Map<String, Object> auth = new LinkedHashMap<>();
                    auth.put("tjanstekonsument", consumerHsaId);
                    auth.put("logiskAdress", laHsaId);
                    auth.put("tjanstekontrakt", contractRow[1]);
                    authorizations.add(auth);
                }
            }
        }

        Map<String, Object> section = new LinkedHashMap<>();
        section.put("tjanstekomponenter", new ArrayList<>(components));
        section.put("tjanstekontrakt", new ArrayList<>(contracts));
        section.put("logiskadresser", new ArrayList<>(logicalAddresses));
        section.put("anropsbehorigheter", new ArrayList<>(authorizations));
        section.put("vagval", new ArrayList<>(routings));
        return section;
    }

    private static Map<String, Object> component(String hsaId, String description) {
        Map<String, Object> component = new LinkedHashMap<>();
        component.put("hsaId", hsaId);
        component.put("beskrivning", nullToEmpty(description));
        return component;
    }

    private static int majorVersion(String namespace) {
        try {
            return Integer.parseInt(namespace.substring(namespace.lastIndexOf(':') + 1));
        } catch (NumberFormatException e) {
            throw new MasstakningException("Kunde inte läsa ut major-version från namnrymden " + namespace
                    + " i fliken " + SHEET_CONTRACTS + ".");
        }
    }

    private static String required(String value, String sheet, String field) {
        if (value == null) {
            throw new MasstakningException(
                    "Värde för " + field + " saknas i fliken " + sheet + ". " + MSG_INVALID_FILE);
        }
        return value;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /**
     * Reads data rows (from row 3) of a sheet. Blank cells are returned as null and rows where all read cells are
     * blank are skipped.
     */
    private static class SheetReader {
        private final Workbook workbook;
        private final DataFormatter formatter = new DataFormatter();
        private final FormulaEvaluator evaluator;

        SheetReader(Workbook workbook) {
            this.workbook = workbook;
            this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        }

        List<String[]> read(String sheetName, int columns) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new MasstakningException("Fliken " + sheetName + " saknas. " + MSG_INVALID_FILE);
            }
            List<String[]> rows = new ArrayList<>();
            for (int r = FIRST_DATA_ROW; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                String[] values = new String[columns];
                boolean blank = true;
                for (int c = 0; c < columns; c++) {
                    values[c] = cellValue(row.getCell(c));
                    blank &= values[c] == null;
                }
                if (!blank) {
                    rows.add(values);
                }
            }
            return rows;
        }

        private String cellValue(Cell cell) {
            if (cell == null) {
                return null;
            }
            String value = formatter.formatCellValue(cell, evaluator);
            return value.isEmpty() ? null : value;
        }
    }
}
