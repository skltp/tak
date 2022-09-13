package se.skltp.tak.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.EntityService;
import se.skltp.tak.web.service.RivTaProfilService;
import se.skltp.tak.web.service.TjanstekontraktService;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CrudController {

    @Autowired
    RivTaProfilService rivTaProfilService;

    @Autowired
    TjanstekontraktService tjanstekontraktService;

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
        return entity + "/show";
    }

    @GetMapping("/{entity}/create")
    public String create(Model model, @PathVariable String entity) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        model.addAttribute("instance", createEntity(entity));
        return entity + "/create";
    }

    @PostMapping("/rivTaProfil/create")
    public String save(@Valid @ModelAttribute("rivTaProfilInstance")RivTaProfil instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("rivTaProfil", instance, result, attributes);
    }

    @GetMapping("/{entity}/edit/{id}")
    public String edit(@PathVariable String entity, Model model, @PathVariable Long id) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("instance", instance.get());
        return entity + "/edit";
    }

    @PostMapping("/rivTaProfil/update")
    public String update(@Valid @ModelAttribute("instance")RivTaProfil instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("rivTaProfil", instance, result, attributes);
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
            return "error";
        }
        AbstractVersionInfo newInstance = getService(entity).add(instance, "User");
        attributes.addFlashAttribute("message", entity + " skapad");
        return "redirect:/" + entity;
    }

    private String update(String entity, AbstractVersionInfo instance,
                          BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "error";
        }
        AbstractVersionInfo newInstance = getService(entity).update(instance, "User");
        attributes.addFlashAttribute("message", entity + " uppdaterad");
        return "redirect:/" + entity;
    }

    private EntityService getService(String entityKey) {
        switch (entityKey) {

            case "rivTaProfil": return rivTaProfilService;
            case "tjanstekontrakt": return tjanstekontraktService;
            default: throw new IllegalArgumentException();
        }
    }

    private AbstractVersionInfo createEntity(String entityKey) {
        switch (entityKey) {
            case "rivTaProfil": return new RivTaProfil();
            case "tjanstekontrakt": return new Tjanstekontrakt();
            default: throw new IllegalArgumentException();
        }
    }
}