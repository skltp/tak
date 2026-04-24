/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import static se.skltp.tak.web.util.SecurityUtil.getUserName;

@ControllerAdvice
public class SessionInfoControllerAdvice {

    @ModelAttribute
    public void addUserInfoToModel(Model model) {
        model.addAttribute("username", getUserName());
    }
}