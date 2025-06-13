package se.skltp.tak.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    BestallningService bestallningService;

    @Autowired
    BestallningsStodetConnectionService bestallningsStodetConnectionService;

    @GetMapping("/bestallning")
    public String create(Model model) {
        boolean bestallningOn = bestallningsStodetConnectionService.isActive();
        if (bestallningOn) {
            Set<String> configErrors = bestallningsStodetConnectionService.checkBestallningConfiguration();
            if (!configErrors.isEmpty()) {
                model.addAttribute("errors", configErrors);
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
            model.addAttribute("bestallningsNummer", bestallningsNummer);
            json = bestallningsStodetConnectionService.getBestallningByUrl(bestallningsNummer, url);
            model.addAttribute("bestallningJson", json);
        } catch (CustomSSLConfigurationException ex) {
            model.addAttribute("errors", ex.getMessage());
            return create(model);
        } catch (RuntimeException re) {
            String errorMessage = getString(bestallningsNummer, url, re);
            log.error("Runtime error while processing Beställning {} at URL {}: {}", bestallningsNummer, url, errorMessage, re);
            model.addAttribute("errors", Collections.singletonList(errorMessage));
            model.addAttribute("url", url);
            return create(model);
        } catch (Exception e) {
            String error = String.format("Kunde inte hämta beställning %d från beställningsstödet.", bestallningsNummer);
            log.error(error, e);
            model.addAttribute("errors", Collections.singletonList(error));
            model.addAttribute("url", url);
            return create(model);
        }

        try {
            String formatted = bestallningService.parseAndFormatJson(json);
            model.addAttribute("bestallningJson", formatted);
            model.addAttribute("url", url);
        } catch (Exception e) {
            String error = String.format("Beställning %d kunde inte tolkas: %s", bestallningsNummer, e);
            log.error(error, e);
            model.addAttribute("errors", Collections.singletonList(error));
            model.addAttribute("bestallningJson", json);
        }

        return create(model);
    }

    private static String getString(Long bestallningsNummer, String url, RuntimeException re) {
        String errorMessage;
        Throwable cause = re.getCause();

        if (cause instanceof FileNotFoundException) {
            errorMessage = String.format("Resursen hittades inte %s för beställning %d.", url, bestallningsNummer);
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
                attributes.addFlashAttribute("errors", data.getBestallningErrors());
                attributes.addFlashAttribute("bestallningJson", bestallningJson);
                if (bestallningsNummer != null) attributes.addFlashAttribute("bestallningsNummer", bestallningsNummer);
                return "redirect:/bestallning";
            }
            BestallningsRapport rapport = data.getBestallningsRapport();
            model.addAttribute("metadata", rapport.getMetadata());
            model.addAttribute("inkludera", rapport.getInkludera());
            model.addAttribute("exkludera", rapport.getExkludera());
            model.addAttribute("bestallningHash", data.hashCode());
            request.getSession().setAttribute("bestallning", bestallningJson);
            return "bestallning/confirm";
        } catch (Exception e) {
            String error = String.format("Fel när beställning %d skulle behandlas: %s", bestallningsNummer, e);
            log.error(error, e);
            attributes.addFlashAttribute("errors", Collections.singletonList(error));
            attributes.addFlashAttribute("bestallningJson", bestallningJson);
            if (bestallningsNummer != null) attributes.addFlashAttribute("bestallningsNummer", bestallningsNummer);
            return "redirect:/bestallning";
        }
    }

    @RequestMapping("/bestallning/cancel")
    public String cancel(HttpServletRequest request, RedirectAttributes attributes) {
        clearBestallningsDataFromSession(request);
        return "redirect:/bestallning";
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
        }
        model.addAttribute("saved", success);
        model.addAttribute("report", report);
        clearBestallningsDataFromSession(request);
        return "bestallning/save";
    }

    private BestallningsData getBestallningsDataFromSession(HttpServletRequest request, String bestallningHash) throws JsonProcessingException {
        if (request == null || request.getSession() == null || bestallningHash == null) return null;
        String jsondatastring = request.getSession().getAttribute("bestallning").toString();
        BestallningsData data = bestallningService.buildBestallningsData(jsondatastring, getUserName());
        if (!bestallningHash.equals(Integer.toString(data.hashCode()))) return null;
        return data;
    }

    private void clearBestallningsDataFromSession(HttpServletRequest request) {
        request.getSession().setAttribute("bestallning", null);
    }

    @ExceptionHandler(CustomSSLConfigurationException.class)
    public ResponseEntity<String> handleSSLConfigurationException(CustomSSLConfigurationException ex) {
        log.error("SSL Configuration Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An SSL configuration error occurred: " + ex.getMessage() +
                        ". Please contact the support team.");
    }

}
