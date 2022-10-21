package se.skltp.tak.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BestallningServiceTests {

    BestallningService service = new BestallningService();

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
    public void testMetadata() throws Exception {
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
}
