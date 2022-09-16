package se.skltp.tak.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.EntityService;
import se.skltp.tak.web.service.RivTaProfilService;
import se.skltp.tak.web.service.TjanstekomponentService;
import se.skltp.tak.web.service.TjanstekontraktService;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CrudController {

    @Autowired
    RivTaProfilService rivTaProfilService;

    @Autowired
    TjanstekontraktService tjanstekontraktService;

    @Autowired
    TjanstekomponentService tjanstekomponentService;

    @RequestMapping("/{entity}")
    public String index(@PathVariable String entity, Model model, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "10") Integer max) {
        if (entity == null || entity.length() == 0 ) return "home/index";
        model.addAttribute("entityName", getService(entity).getEntityName());
        PagedEntityList list = getService(entity).getEntityList(offset, max);
        model.addAttribute("list", list);
        model.addAttribute("basePath", "/" + entity);
        return entity + "/list";
    }

    @RequestMapping("/{entity}/{id}")
    public String show(@PathVariable String entity, Model model, @PathVariable Long id) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/" + entity);
        return entity + "/show";
    }

    @GetMapping("/{entity}/create")
    public String create(Model model, @PathVariable String entity) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        model.addAttribute("instance", getService(entity).createEntity());
        model.addAttribute("basePath", "/" + entity);
        return entity + "/create";
    }

    @PostMapping("/rivTaProfil/create")
    public String save(@Valid @ModelAttribute("instance")RivTaProfil instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("rivTaProfil", instance, result, attributes);
    }

    @PostMapping("/tjanstekontrakt/create")
    public String save(@Valid @ModelAttribute("instance")Tjanstekontrakt instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("tjanstekontrakt", instance, result, attributes);
    }

    @PostMapping("/tjanstekomponent/create")
    public String save(@Valid @ModelAttribute("instance")Tjanstekomponent instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("tjanstekomponent", instance, result, attributes);
    }

    @GetMapping("/{entity}/edit/{id}")
    public String edit(@PathVariable String entity, Model model, @PathVariable Long id) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/" + entity);
        return entity + "/edit";
    }

    @PostMapping("/rivTaProfil/update")
    public String update(@Valid @ModelAttribute("instance")RivTaProfil instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("rivTaProfil", instance, result, attributes);
    }

    @PostMapping("/tjanstekontrakt/update")
    public String update(@Valid @ModelAttribute("instance")Tjanstekontrakt instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("tjanstekontrakt", instance, result, attributes);
    }

    @PostMapping("/tjanstekomponent/update")
    public String update(@Valid @ModelAttribute("instance") Tjanstekomponent instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("tjanstekomponent", instance, result, attributes);
    }

    @PostMapping("/{entity}/delete")
    public String delete(@PathVariable String entity, @RequestParam Long id, RedirectAttributes attributes) {
        if (getService(entity).delete(id, "User")) {
            attributes.addFlashAttribute("message", entity + " borttagen");
        }
        else {
            return "error";
        }
        return "redirect:/" + entity;
    }

    private String save(String entity, AbstractVersionInfo instance,
                        BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return entity + "/create";
        }
        try {
            AbstractVersionInfo newInstance = getService(entity).add(instance, "User");
            attributes.addFlashAttribute("message", entity + " skapad");
            return "redirect:/" + entity;
        }
        catch (Exception e) {
            result.addError(new ObjectError("globalError", e.toString()));
            return entity + "/create";
        }
    }

    private String update(String entity, AbstractVersionInfo instance,
                          BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return entity + "/edit";
        }
        try {
            AbstractVersionInfo newInstance = getService(entity).update(instance, "User");
            attributes.addFlashAttribute("message", entity + " uppdaterad");
            return "redirect:/" + entity;
        }
        catch (Exception e) {
            result.addError(new ObjectError("globalError", e.toString()));
            return entity + "/edit";
        }
    }

    private EntityService getService(String entityKey) {
        switch (entityKey) {

            case "rivTaProfil": return rivTaProfilService;
            case "tjanstekontrakt": return tjanstekontraktService;
            case "tjanstekomponent": return tjanstekomponentService;
            default: throw new IllegalArgumentException();
        }
    }
}