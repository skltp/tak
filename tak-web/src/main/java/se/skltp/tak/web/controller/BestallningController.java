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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.web.configuration.FormPostSizeFilter;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.exception.CustomSSLConfigurationException;
import se.skltp.tak.web.exception.MasstakningException;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.BestallningsStodetConnectionService;
import se.skltp.tak.web.service.MasstakningService;
import se.skltp.tak.web.util.PythonStyleJsonWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static se.skltp.tak.web.util.SecurityUtil.checkAdministratorRole;
import static se.skltp.tak.web.util.SecurityUtil.getUserName;
import static se.skltp.tak.web.util.SecurityUtil.isAdministrator;

@Controller
public class BestallningController {

    private static final Logger log = LoggerFactory.getLogger(BestallningController.class);

    private static final String ATTR_ERRORS = "errors";
    private static final String ATTR_BESTALLNINGS_NUMMER = "bestallningsNummer";
    private static final String ATTR_BESTALLNING_JSON = "bestallningJson";
    private static final String REDIRECT_BESTALLNING = "redirect:/bestallning";
    private static final String SESSION_BESTALLNING = "bestallning";

    private final BestallningService bestallningService;
    private static final String DEFAULT_MASSTAKNING_PLATTFORM = "NTJP-PROD";

    private final BestallningsStodetConnectionService bestallningsStodetConnectionService;
    private final MasstakningService masstakningService;
    private final long maxFormPostSize;

    public BestallningController(BestallningService bestallningService,
                                 BestallningsStodetConnectionService bestallningsStodetConnectionService,
                                 MasstakningService masstakningService,
                                 @Value("${server.tomcat.max-http-form-post-size:2MB}") DataSize maxFormPostSize) {
        this.bestallningService = bestallningService;
        this.bestallningsStodetConnectionService = bestallningsStodetConnectionService;
        this.masstakningService = masstakningService;
        this.maxFormPostSize = maxFormPostSize.toBytes();
    }

    @GetMapping(value = "/bestallning", params = FormPostSizeFilter.TOO_LARGE_PARAM)
    public String createAfterTooLargeOrder(Model model, @RequestParam(FormPostSizeFilter.TOO_LARGE_PARAM) long size) {
        model.addAttribute(ATTR_ERRORS, Collections.singletonList(
                FormPostSizeFilter.tooLargeMessage(size, maxFormPostSize)));
        return create(model);
    }

    @GetMapping("/bestallning")
    public String create(Model model) {
        model.addAttribute("maxBestallningSize", maxFormPostSize);
        addMasstakningAttributes(model);
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

    /**
     * Mass-TAKning on the Beställning page (admin only): creates a TakOrder from an Excel file, in the same way as
     * the MkRoutingFromExcel.py script, and puts it in the Beställning box.
     */
    @PostMapping("/bestallning/masstakning")
    public String createFromMasstakning(Model model,
                                        @RequestParam(required = false) MultipartFile file,
                                        @RequestParam(defaultValue = DEFAULT_MASSTAKNING_PLATTFORM) String plattform,
                                        @RequestParam(defaultValue = MasstakningService.ACTION_TAKA) String action) {
        checkAdministratorRole();
        model.addAttribute("masstakningPlattform", plattform);
        model.addAttribute("masstakningAction", action);
        try {
            if (file == null || file.isEmpty()) {
                throw new MasstakningException("Ingen excel-fil har angetts.");
            }
            Map<String, Object> takOrder = masstakningService.createTakOrder(file.getBytes(), plattform, action);
            model.addAttribute(ATTR_BESTALLNING_JSON, PythonStyleJsonWriter.write(takOrder));
            model.addAttribute("masstakningMessage", String.format(
                    "Beställning skapad från %s (%s, %s). Kontrollera och tryck på Granska.",
                    file.getOriginalFilename(), plattform, action));
            log.info("User {} created mass-TAKning order on Beställning ({} {}) from {}",
                    getUserName(), action, plattform, file.getOriginalFilename());
        } catch (MasstakningException e) {
            model.addAttribute("masstakningErrors", Collections.singletonList(e.getMessage()));
        } catch (IOException e) {
            log.error("Could not handle mass-TAKning file", e);
            model.addAttribute("masstakningErrors",
                    Collections.singletonList("Kunde inte läsa excel-filen: " + e.getMessage()));
        }
        return create(model);
    }

    private void addMasstakningAttributes(Model model) {
        boolean administrator = isAdministrator();
        model.addAttribute("administrator", administrator);
        if (!administrator) return;
        model.addAttribute("masstakningPlattformar", MasstakningService.PLATTFORMAR);
        model.addAttribute("masstakningActions", List.of(MasstakningService.ACTION_TAKA, MasstakningService.ACTION_AVTAKA));
        if (!model.containsAttribute("masstakningPlattform")) {
            model.addAttribute("masstakningPlattform", DEFAULT_MASSTAKNING_PLATTFORM);
        }
        if (!model.containsAttribute("masstakningAction")) {
            model.addAttribute("masstakningAction", MasstakningService.ACTION_TAKA);
        }
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

    /**
     * The order is missing, e.g. when a too large form post without Content-Length was dropped by Tomcat.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParameter(MissingServletRequestParameterException ex, RedirectAttributes attributes) {
        log.warn("Missing request parameter: {}", ex.getMessage());
        attributes.addFlashAttribute(ATTR_ERRORS, Collections.singletonList(String.format(
                "Beställningen saknas eller är för stor. Maximal storlek är %s.",
                FormPostSizeFilter.formatMegabytes(maxFormPostSize))));
        return REDIRECT_BESTALLNING;
    }

    @ExceptionHandler(CustomSSLConfigurationException.class)
    public ResponseEntity<String> handleSSLConfigurationException(CustomSSLConfigurationException ex) {
        log.error("SSL Configuration Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An SSL configuration error occurred: " + ex.getMessage() +
                        ". Please contact the support team.");
    }

}
