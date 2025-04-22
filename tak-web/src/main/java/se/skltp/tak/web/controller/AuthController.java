package se.skltp.tak.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static java.lang.Boolean.TRUE;

@Controller
public class AuthController {

    @GetMapping("/auth/login")
    public String login(Model model, @RequestParam(required = false) Boolean error, @RequestParam(required = false) Boolean csrfError) {
        if (TRUE.equals(error)) {
            model.addAttribute("message", "Ogiltigt användarnamn och/eller lösenord");
        }
        if (TRUE.equals(csrfError)) {
            model.addAttribute("message", "felaktigt/utdaterat CSRF-token, försök igen");
        }


        return "auth/login";
    }
}
