package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.service.BestallningService;

import java.net.URLDecoder;

@RestController
public class BestallningRestController {

    @Autowired
    BestallningService bestallningService;

    @Autowired

    private final static Logger log = LoggerFactory.getLogger(BestallningRestController.class);

    /**
     * REST-ish endpoint for scripted order handling. See resources/bin/jsonScripts for usage examples.
     */
    @PostMapping(value = "/rest/create", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE},
            produces = "text/plain")
    String create(@RequestBody String bestallningJson) {
        String parsedJson;
        try {
            String decodedJson = URLDecoder.decode(bestallningJson, "UTF-8");
            log.info("Beställning:\n {}", decodedJson);
            parsedJson = bestallningService.parseAndFormatJson(decodedJson);
        } catch (Exception e) {
            String error = String.format("Fel vid tolkning av beställning: {}", e.getMessage());
            log.error(error);
            return error;
        }
        try {
            BestallningsData data = bestallningService.buildBestallningsData(parsedJson, getUserName());
            if (data.hasErrors()) {
                String error = String.format("Fel i beställningsdata: {}", data.getBestallningErrors());
                log.error(error);
                return error;
            }
            bestallningService.execute(data, getUserName());
        } catch (Exception e) {
            String error = String.format("Fel vid genomförande av beställning: {}", e.getMessage());
            log.error(error);
            return error;
        }
        return "Beställning klar";
    }

    private String getUserName() {
        Subject subject = SecurityUtils.getSubject();
        return subject.getPrincipal().toString();
    }
}
