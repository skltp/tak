/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.exception.CustomSSLConfigurationException;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.BestallningsStodetConnectionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static se.skltp.tak.web.util.SecurityUtil.getUserName;

@Controller
public class BestallningController {

    private static final Logger log = LoggerFactory.getLogger(BestallningController.class);

    private static final String ATTR_ERRORS = "errors";
    private static final String ATTR_BESTALLNINGS_NUMMER = "bestallningsNummer";
    private static final String ATTR_BESTALLNING_JSON = "bestallningJson";
    private static final String REDIRECT_BESTALLNING = "redirect:/bestallning";
    private static final String SESSION_BESTALLNING = "bestallning";

    private final BestallningService bestallningService;
    private final BestallningsStodetConnectionService bestallningsStodetConnectionService;

    public BestallningController(BestallningService bestallningService,
                                 BestallningsStodetConnectionService bestallningsStodetConnectionService) {
        this.bestallningService = bestallningService;
        this.bestallningsStodetConnectionService = bestallningsStodetConnectionService;
    }

    @GetMapping("/bestallning")
    public String create(Model model) {
        boolean bestallningOn = bestallningsStodetConnectionService.isActive();
        if (bestallningOn) {
            Set<String> configErrors = bestallningsStodetConnectionService.checkBestallningConfiguration();
            if (!configErrors.isEmpty()) {
                model.addAttribute(ATTR_ERRORS, configErrors);
                bestallningOn = false;
            }
        } else {
            model.addAttribute("message", "Hämtning av beställning via beställningsnummer är avstängt.");
        }
        model.addAttribute("bestallningOn", bestallningOn);
        if (bestallningOn) {
            List<Map<String, String>> bestallningUrlsWithNames = bestallningsStodetConnectionService.getBestallningUrlsWithNames();
            model.addAttribute("bestallningUrlsWithNames", bestallningUrlsWithNames);
        }
        return "bestallning/create";
    }

    @PostMapping("/bestallning")
    public String createFromOrderId(Model model, @RequestParam(required = false) Long bestallningsNummer, @RequestParam String url) {
        String json;

        try {
            model.addAttribute(ATTR_BESTALLNINGS_NUMMER, bestallningsNummer);
            json = bestallningsStodetConnectionService.getBestallningByUrl(bestallningsNummer, url);
            model.addAttribute(ATTR_BESTALLNING_JSON, json);
        } catch (CustomSSLConfigurationException ex) {
            model.addAttribute(ATTR_ERRORS, ex.getMessage());
            return create(model);
        } catch (RuntimeException re) {
            String errorMessage = getString(bestallningsNummer, url, re);
            log.error("Runtime error while processing Beställning {} at URL {}: {}", bestallningsNummer, url, errorMessage, re);
            model.addAttribute(ATTR_ERRORS, Collections.singletonList(errorMessage));
            model.addAttribute("url", url);
            return create(model);
        } catch (Exception e) {
            String error = String.format("Kunde inte hämta beställning %d från beställningsstödet.", bestallningsNummer);
            log.error(error, e);
            model.addAttribute(ATTR_ERRORS, Collections.singletonList(error));
            model.addAttribute("url", url);
            return create(model);
        }

        try {
            String formatted = bestallningService.parseAndFormatJson(json);
            model.addAttribute(ATTR_BESTALLNING_JSON, formatted);
            model.addAttribute("url", url);
        } catch (Exception e) {
            String error = String.format("Beställning %d kunde inte tolkas: %s", bestallningsNummer, e);
            log.error(error, e);
            model.addAttribute(ATTR_ERRORS, Collections.singletonList(error));
            model.addAttribute(ATTR_BESTALLNING_JSON, json);
        }

        return create(model);
    }

    private static String getString(Long bestallningsNummer, String url, RuntimeException re) {
        String errorMessage;
        Throwable cause = re.getCause();

        if (cause instanceof FileNotFoundException) {
            errorMessage = String.format("Beställning %d hittades inte på %s.", bestallningsNummer, url);
        } else if (cause instanceof ConnectException) {
            errorMessage = String.format("Anslutningen nekades %s för beställning %d.", url, bestallningsNummer);
        } else if (cause instanceof UnknownHostException) {
            errorMessage = String.format("Okänd värd %s för beställning %d.", url, bestallningsNummer);
        } else {
            errorMessage = String.format("Ett okänt fel uppstod vid hämtning av beställning %d: %s.",
                    bestallningsNummer, cause != null ? cause.getMessage() : re.getMessage());
        }
        return errorMessage;
    }

    @PostMapping("/bestallning/confirm")
    public String confirm(HttpServletRequest request, Model model, @RequestParam String bestallningJson,
                          @RequestParam(defaultValue = "") Long bestallningsNummer, RedirectAttributes attributes) {
        try {
            BestallningsData data = bestallningService.buildBestallningsData(bestallningJson, getUserName());
            if (data.hasErrors()) {
                attributes.addFlashAttribute(ATTR_ERRORS, data.getBestallningErrors());
                attributes.addFlashAttribute(ATTR_BESTALLNING_JSON, bestallningJson);
                if (bestallningsNummer != null) attributes.addFlashAttribute(ATTR_BESTALLNINGS_NUMMER, bestallningsNummer);
                return REDIRECT_BESTALLNING;
            }
            BestallningsRapport rapport = data.getBestallningsRapport();
            model.addAttribute("metadata", rapport.getRapportHuvud());
            model.addAttribute("inkludera", rapport.getInkludera());
            model.addAttribute("exkludera", rapport.getExkludera());
            model.addAttribute("bestallningHash", data.hashCode());
            request.getSession().setAttribute(SESSION_BESTALLNING, bestallningJson);
            return "bestallning/confirm";
        } catch (Exception e) {
            String error = String.format("Fel när beställning %d skulle behandlas: %s", bestallningsNummer, e);
            log.error(error, e);
            attributes.addFlashAttribute(ATTR_ERRORS, Collections.singletonList(error));
            attributes.addFlashAttribute(ATTR_BESTALLNING_JSON, bestallningJson);
            if (bestallningsNummer != null) attributes.addFlashAttribute(ATTR_BESTALLNINGS_NUMMER, bestallningsNummer);
            return REDIRECT_BESTALLNING;
        }
    }

    @GetMapping("/bestallning/cancel")
    public String cancel(HttpServletRequest request) {
        clearBestallningsDataFromSession(request);
        return REDIRECT_BESTALLNING;
    }

    @PostMapping("/bestallning/save")
    public String save(HttpServletRequest request, Model model, @RequestParam String bestallningHash) throws JsonProcessingException {
        boolean success = false;
        String report = "";
        BestallningsData data = getBestallningsDataFromSession(request, bestallningHash);
        if (data != null) {
            bestallningService.execute(data, getUserName());
            report = data.getBestallningsRapport().toString();
            success = true;
        } else {
            model.addAttribute("message",
                    "Beställningen kunde inte sparas. Den är antingen redan sparad eller så har sessionen avslutats. " +
                            "Gå till Ny Beställning för att skapa en ny beställning.");
        }
        model.addAttribute("saved", success);
        model.addAttribute("report", report);
        clearBestallningsDataFromSession(request);
        return "bestallning/save";
    }

    private BestallningsData getBestallningsDataFromSession(HttpServletRequest request, String bestallningHash) throws JsonProcessingException {
        if (request == null || bestallningHash == null) return null;
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object jsondata = session.getAttribute(SESSION_BESTALLNING);
        if (jsondata == null) {
            log.info("Ingen beställning hittades i sessionen. Beställningen är troligen redan sparad, avbruten eller så har sessionen gått ut.");
            return null;
        }
        BestallningsData data = bestallningService.buildBestallningsData(jsondata.toString(), getUserName());
        if (data == null || !bestallningHash.equals(Integer.toString(data.hashCode()))) return null;
        return data;
    }

    private void clearBestallningsDataFromSession(HttpServletRequest request) {
        if (request == null) return;
        HttpSession session = request.getSession(false);
        if (session != null) session.removeAttribute(SESSION_BESTALLNING);
    }

    @ExceptionHandler(CustomSSLConfigurationException.class)
    public ResponseEntity<String> handleSSLConfigurationException(CustomSSLConfigurationException ex) {
        log.error("SSL Configuration Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An SSL configuration error occurred: " + ex.getMessage() +
                        ". Please contact the support team.");
    }

}
