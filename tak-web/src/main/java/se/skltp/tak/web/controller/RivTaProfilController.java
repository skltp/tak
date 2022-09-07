package se.skltp.tak.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.service.RivTaProfilService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class RivTaProfilController {

    @Autowired
    RivTaProfilService service;

    @RequestMapping("/rivTaProfil")
    public String index(Model model) {
        List<RivTaProfil> list = service.findNotDeleted();
        model.addAttribute("rivTaProfilInstanceList", list);
        return "rivTaProfil/list";
    }

    @RequestMapping("/rivTaProfil/{id}")
    public String show(Model model, @PathVariable Long id) {
        Optional<RivTaProfil> instance = service.findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("rivTaProfilInstance", instance.get());
        return "rivTaProfil/show";
    }

    @GetMapping("/rivTaProfil/create")
    public String create(Model model) {
        model.addAttribute("rivTaProfilInstance", new RivTaProfil());
        return "rivTaProfil/create";
    }

    @PostMapping("/rivTaProfil/create")
    public String save(@Valid @ModelAttribute("rivTaProfilInstance")RivTaProfil instance,
                               BindingResult result, ModelMap model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "error";
        }
        RivTaProfil newInstance = service.add(instance, "User");
        attributes.addFlashAttribute("message", "RivTaProfil skapad");
        return "redirect:/rivTaProfil/" + newInstance.getId();
    }

    @GetMapping("/rivTaProfil/edit/{id}")
    public String edit(Model model, @PathVariable Long id) {
        Optional<RivTaProfil> instance = service.findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("rivTaProfilInstance", instance.get());
        return "rivTaProfil/edit";
    }

    @PostMapping("/rivTaProfil/update")
    public String update(@Valid @ModelAttribute("rivTaProfilInstance")RivTaProfil instance,
                               BindingResult result, ModelMap model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "error";
        }
        RivTaProfil newInstance = service.update(instance, "User");
        attributes.addFlashAttribute("message", "RivTaProfil uppdaterad");
        return "redirect:/rivTaProfil/" + newInstance.getId();
    }

}