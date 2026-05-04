package se.skltp.tak.web.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import static se.skltp.tak.web.util.SecurityUtil.getUserName;

@ControllerAdvice
@Profile("!oauth-dpop")
public class DefaultSessionInfoControllerAdvice {

    @ModelAttribute
    public void addUserInfoToModel(Model model) {
        model.addAttribute("username", getUserName());
    }
}

