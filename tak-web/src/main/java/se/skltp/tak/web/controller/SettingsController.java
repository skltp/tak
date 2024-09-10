package se.skltp.tak.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.entity.TAKSettings;
import se.skltp.tak.web.service.SettingsService;


import java.util.Optional;

import static se.skltp.tak.web.util.SecurityUtil.checkAdministratorRole;

@Controller
public class SettingsController {

    @Autowired
    SettingsService settingsService;


    @RequestMapping("/settings")
    public String index(Model model, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "10") Integer max) {
        checkAdministratorRole();
        model.addAttribute("entityName", "Inställningar");
        PagedEntityList list = settingsService.getEntityList(offset, max);
        model.addAttribute("list", list);
        model.addAttribute("basePath", "/settings");
        return "settings/list";
    }

    @RequestMapping("/settings/{id}")
    public String show(Model model, @PathVariable Long id) {
        checkAdministratorRole();
        model.addAttribute("entityName", "Inställningar");
        Optional instance = settingsService.findById(id);
        if (!instance.isPresent()) throw new IllegalArgumentException("Setting not found");
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/settings");
        return "settings/show";
    }

    @GetMapping("/settings/edit/{id}")
    public String edit(Model model, @PathVariable Long id) {
        checkAdministratorRole();
        model.addAttribute("entityName", "Inställningar");
        Optional instance = settingsService.findById(id);
        if (!instance.isPresent()) throw new IllegalArgumentException("Setting not found");
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/settings");
        return "settings/edit";
    }

    @PostMapping("/settings/update")
    public String update(@Valid @ModelAttribute("instance") TAKSettings instance,
                         BindingResult result, RedirectAttributes attributes) {
        checkAdministratorRole();
        if (result.hasErrors()) {
            return "settings/edit";
        }
        try {
            TAKSettings newInstance = settingsService.update(instance);
            attributes.addFlashAttribute("message", "Inställning uppdaterad");
            return "redirect:/settings";
        }
        catch (Exception e) {
            result.addError(new ObjectError("globalError", e.toString()));
            return "settings/edit";
        }
    }
}