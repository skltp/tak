package se.skltp.tak.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.dto.bestallning.JsonBestallning;

@Service
public class BestallningService {
    private static final Logger log = LoggerFactory.getLogger(BestallningService.class);

    public String parseAndFormatJson(String jsonInput) throws Exception {
        String jsonBestallning = trimJson(jsonInput);
        ObjectMapper mapper = new ObjectMapper();
        JsonBestallning json = mapper.readValue(jsonBestallning, JsonBestallning.class);
        String formattedBestallning = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        log.info("JsonBestallning: \n " + formattedBestallning);
        checkMandatoryInfo(json);
        return formattedBestallning;
    }

    private String trimJson(String jsonInput) {
        if (jsonInput == null || !jsonInput.contains("{") || !jsonInput.contains("}")) {
            throw new IllegalArgumentException("Bestallning does not contain JSON object");
        }
        return jsonInput.substring(jsonInput.indexOf("{"), jsonInput.lastIndexOf("}") + 1);
    }

    private void checkMandatoryInfo(JsonBestallning json) {
        if (json.getPlattform() == null) {
            throw new IllegalArgumentException("Bestallning is missing mandatory field: plattform");
        }
    }
}
