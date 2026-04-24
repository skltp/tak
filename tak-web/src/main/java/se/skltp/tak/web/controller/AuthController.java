/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
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
