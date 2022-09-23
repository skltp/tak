package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.service.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AnvandareController {

    @Autowired
    AnvandareService anvandareService;


    @RequestMapping("/anvandare")
    public String index(Model model, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "10") Integer max) {
        checkAdministratorRole();
        model.addAttribute("entityName", "Användare");
        PagedEntityList list = anvandareService.getEntityList(offset, max);
        model.addAttribute("list", list);
        model.addAttribute("basePath", "/anvandare");
        return "anvandare/list";
    }

    @RequestMapping("/anvandare/{id}")
    public String show(Model model, @PathVariable Long id) {
        checkAdministratorRole();
        model.addAttribute("entityName", "Användare");
        Optional instance = anvandareService.findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/anvandare");
        return "anvandare/show";
    }

    @GetMapping("/anvandare/create")
    public String create(Model model) {
        checkAdministratorRole();
        model.addAttribute("entityName", "Användare");
        model.addAttribute("instance", new Anvandare());
        model.addAttribute("basePath", "/anvandare");
        return "anvandare/create";
    }

    @PostMapping("/anvandare/create")
    public String save(@Valid @ModelAttribute("instance")Anvandare instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        checkAdministratorRole();
        if (result.hasErrors()) {
            return "anvandare/create";
        }
        try {
            Anvandare newInstance = anvandareService.add(instance);
            attributes.addFlashAttribute("message", "Användare skapad");
            return "redirect:/anvandare";
        }
        catch (Exception e) {
            result.addError(new ObjectError("globalError", e.toString()));
            return "anvandare/create";
        }
    }

    @GetMapping("/anvandare/edit/{id}")
    public String edit(Model model, @PathVariable Long id) {
        checkAdministratorRole();
        model.addAttribute("entityName", "Användare");
        Optional instance = anvandareService.findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/anvandare");
        return "anvandare/edit";
    }

    @PostMapping("/anvandare/update")
    public String update(@Valid @ModelAttribute("instance") Anvandare instance,
                         BindingResult result, RedirectAttributes attributes) {
        checkAdministratorRole();
        if (result.hasErrors()) {
            return "anvandare/edit";
        }
        try {
            Anvandare newInstance = anvandareService.update(instance);
            attributes.addFlashAttribute("message", "Användare uppdaterad");
            return "redirect:/anvandare";
        }
        catch (Exception e) {
            result.addError(new ObjectError("globalError", e.toString()));
            return "anvandare/edit";
        }
    }

    @PostMapping("/anvandare/delete")
    public String delete(@RequestParam long id, @RequestParam long version, RedirectAttributes attributes) {
        checkAdministratorRole();
        if (anvandareService.delete(id, version)) {
            attributes.addFlashAttribute("message", "Användare borttagen");
        }
        else {
            return "error";
        }
        return "redirect:/anvandare";
    }

    private void checkAdministratorRole() {
        if(!SecurityUtils.getSubject().hasRole("Administrator")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}