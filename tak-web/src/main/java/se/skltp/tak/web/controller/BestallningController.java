package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.BestallningsRapport;
import se.skltp.tak.web.service.BestallningService;
import se.skltp.tak.web.service.BestallningsStodetConnectionService;

@Controller
public class BestallningController {

    private static final Logger log = LoggerFactory.getLogger(BestallningsStodetConnectionService.class);

    @Autowired
    BestallningService bestallningService;

    @Autowired
    BestallningsStodetConnectionService bestallningsStodetConnectionService;

    @GetMapping("/bestallning")
    public String create() {
        return "bestallning/create";
    }

    @PostMapping("/bestallning")
    public String createFromOrderId(Model model, @RequestParam Long bestallningsNummer) {
        String json;
        try {
            model.addAttribute("bestallningsNummer", bestallningsNummer);
            json = bestallningsStodetConnectionService.getBestallning(bestallningsNummer);
            model.addAttribute("bestallningJson", json);
        }
        catch (Exception e) {
            log.error("Failed to get order number {}, exception: {}", bestallningsNummer, e.getMessage());
            return "bestallning/create";
        }

        try {
            String formatted = bestallningService.parseAndFormatJson(json);
            model.addAttribute("bestallningJson", formatted);
        }
        catch (Exception e) {
            model.addAttribute("bestallningJson", json);
            log.error("Fetched json has errors, exception: {}", e.getMessage());
        }
        return "bestallning/create";
    }

    @PostMapping("/bestallning/confirm")
    public String confirm(Model model, @RequestParam String bestallningJson) {
        try {
            BestallningsData data = bestallningService.buildBestallningsData(bestallningJson, getUserName());
            BestallningsRapport rapport = new BestallningsRapport(data);
            model.addAttribute("metadata", rapport.getMetadata());
            model.addAttribute("inkludera", rapport.getInkludera());
            model.addAttribute("exkludera", rapport.getExkludera());
            model.addAttribute("bestallningHash", data.hashCode());
            return "bestallning/confirm";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "bestallning/create";
        }
    }

    private String getUserName() {
        Subject subject = SecurityUtils.getSubject();
        return subject.getPrincipal().toString();
    }
}
