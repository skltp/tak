package se.skltp.tak.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/auth/login")
    public String login(Model model, @RequestParam(required = false) Boolean error) {
        if (error.equals(true)) {
            model.addAttribute("message", "Ogiltigt användarnamn och/eller lösenord");
        }

        return "auth/login";
    }
}
