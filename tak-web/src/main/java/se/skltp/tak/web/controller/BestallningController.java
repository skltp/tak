package se.skltp.tak.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.BestallningsStodetConnectionService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
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
        }
        else {
            model.addAttribute("message", "Hämtning av beställning via beställningsnummer är avstängt.");
        }
        model.addAttribute("bestallningOn", bestallningOn);
        if (bestallningOn) {
            List<String> bestallningUrls = bestallningsStodetConnectionService.getBestallningsStodetUrls();
            model.addAttribute("bestallningUrls", bestallningUrls);
        }
        return "bestallning/create";
    }

    @PostMapping("/bestallning")
    public String createFromOrderId(Model model, @RequestParam(required = false) Long bestallningsNummer, @RequestParam(defaultValue = "0") int urlIndex) {
        String json;
        try {
            model.addAttribute("bestallningsNummer", bestallningsNummer);
            json = bestallningsStodetConnectionService.getBestallning(bestallningsNummer, urlIndex);
            model.addAttribute("bestallningJson", json);
        }
        catch (Exception e) {
            String error = String.format("Kunde inte hämta beställning %d från beställningsstödet.", bestallningsNummer);
            log.error(error, e);
            model.addAttribute("errors", Collections.singletonList(error));
            return create(model);
        }

        try {
            String formatted = bestallningService.parseAndFormatJson(json);
            model.addAttribute("bestallningJson", formatted);
        }
        catch (Exception e) {
            String error = String.format("Beställning %d kunde inte tolkas: %s", bestallningsNummer, e);
            log.error(error, e);
            model.addAttribute("errors", Collections.singletonList(error));
            model.addAttribute("bestallningJson", json);
        }
        return create(model);
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
}
