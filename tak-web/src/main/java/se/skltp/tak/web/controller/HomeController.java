/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
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
