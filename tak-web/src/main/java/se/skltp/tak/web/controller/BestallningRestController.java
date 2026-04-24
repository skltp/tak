/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.service.BestallningService;

import java.net.URLDecoder;

import static se.skltp.tak.web.util.SecurityUtil.getUserName;

@RestController
public class BestallningRestController {

    @Autowired
    BestallningService bestallningService;

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
            log.debug("Beställning:\n {}", decodedJson);
            parsedJson = bestallningService.parseAndFormatJson(decodedJson);
        } catch (Exception e) {
            String error = String.format("JsonBestallning ERROR::: {}", e.getMessage());
            log.error(error);
            return error;
        }
        try {
            BestallningsData data = bestallningService.buildBestallningsData(parsedJson, getUserName());
            if (data.hasErrors()) {
                String error = "JsonBestallning ERROR:::";
                for (String be: data.getBestallningErrors()) {
                    error = error + "\n" + be;
                }
                log.error(error);
                return error;
            }
            bestallningService.execute(data, getUserName());
            log.info("JsonBestallning executed::: \n{}", parsedJson);
            return data.getBestallningsRapport().toString();
        } catch (Exception e) {
            log.error("RUNTIME ERROR:::" + e.getCause());
            return "RUNTIME ERROR:::\n" + e.getMessage();
        }
    }
}
