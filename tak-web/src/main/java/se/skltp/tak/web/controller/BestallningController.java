package se.skltp.tak.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        try {
            model.addAttribute("bestallningsNummer", bestallningsNummer);
            String json = bestallningsStodetConnectionService.getBestallning(bestallningsNummer);
            model.addAttribute("bestallningJson", json);
        }
        catch (Exception e) {
            log.error("Failed to get order number {}, exception: {}", bestallningsNummer, e.getMessage());
        }

        return "bestallning/create";
    }
}
