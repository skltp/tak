package se.skltp.tak.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import static se.skltp.tak.web.util.SecurityUtil.checkAdministratorRole;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String index(ModelMap model) {
        try {
            checkAdministratorRole();
            model.addAttribute("administrator", true);
        } catch (ResponseStatusException e) {
            model.addAttribute("administrator", false);
        }

        return "home/index";
    }
}
