package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CrudController {

    @Autowired RivTaProfilService rivTaProfilService;
    @Autowired TjanstekontraktService tjanstekontraktService;
    @Autowired TjanstekomponentService tjanstekomponentService;
    @Autowired LogiskAdressService logiskAdressService;
    @Autowired AnropsAdressService anropsAdressService;
    @Autowired VagvalService vagvalService;

    @GetMapping("/{entity}")
    public String index(@PathVariable String entity,
                        Model model,
                        @RequestParam(defaultValue = "0") Integer offset,
                        @RequestParam(defaultValue = "10") Integer max) {
        if (entity == null || entity.length() == 0 ) {
            return "home/index";
        }
        model.addAttribute("entityName", getService(entity).getEntityName());
        PagedEntityList list = getService(entity).getEntityList(offset, max);
        model.addAttribute("list", list);
        model.addAttribute("basePath", "/" + entity);
        return entity + "/list";
    }

    @GetMapping("/{entity}/{id}")
    public String show(@PathVariable String entity, Model model, @PathVariable Long id) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/" + entity);
        return entity + "/show";
    }

    /**
     * This function serves the webpages used for creation through generalized type switching.
     * @param model
     * @param entity
     * @return
     */
    @GetMapping("/{entity}/create")
    public String create(Model model, @PathVariable String entity) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        model.addAttribute("instance", getService(entity).createEntity());
        model.addAttribute("basePath", "/" + entity);
        return entity + "/create";
    }

    // region CREATION
    /****************
    CREATION via POST
    ****************/

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

    @PostMapping("/logiskAdress/create")
    public String save(@Valid @ModelAttribute("instance")LogiskAdress instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("logiskadress", instance, result, attributes);
    }

    @PostMapping("/anropsadress/create")
    public String save(@Valid @ModelAttribute("instance")AnropsAdress instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("anropsadress", instance, result, attributes);
    }

    @PostMapping("/vagval/create")
    public String save(@Valid @ModelAttribute("instance")Vagval instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("vagval", instance, result, attributes);
    }
    // endregion

    // region EDIT by ID
    /******************************
     SERVE EDIT PAGE for ID via GET
     *****************************/

    @GetMapping("/{entity}/edit/{id}")
    public String edit(@PathVariable String entity, Model model, @PathVariable Long id) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) return "error";
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/" + entity);
        return entity + "/edit";
    }
    // endregion

    // region UPDATE
    /***************
     UPDATE via POST
     **************/

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

    @PostMapping("/logiskAdress/update")
    public String update(@Valid @ModelAttribute("instance") LogiskAdress instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("logiskadress", instance, result, attributes);
    }

    @PostMapping("/anropsadress/update")
    public String update(@Valid @ModelAttribute("instance") AnropsAdress instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("anropsadress", instance, result, attributes);
    }

    @PostMapping("/vagval/update")
    public String update(@Valid @ModelAttribute("instance") Vagval instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("vagval", instance, result, attributes);
    }
    // endregion

    // region DELETION
    /*****************
     DELETION via POST
     *****************/

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
    // endregion

    // region PRIVATE HELPERS
    /************************
     PRIVATE HELPER FUNCTIONS
     ***********************/

    private String save(String entity, AbstractVersionInfo instance,
                        BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return entity + "/create";
        }
        try {
            AbstractVersionInfo newInstance = getService(entity).add(instance, getUserName());
            attributes.addFlashAttribute("message", getService(entity).getEntityName() + " skapad");
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
            AbstractVersionInfo newInstance = getService(entity).update(instance, getUserName());
            attributes.addFlashAttribute("message", getService(entity).getEntityName() + " uppdaterad");
            return "redirect:/" + entity;
        }
        catch (Exception e) {
            result.addError(new ObjectError("globalError", e.toString()));
            return entity + "/edit";
        }
    }

    private String getUserName() {
        Subject subject = SecurityUtils.getSubject();
        return subject.getPrincipal().toString();
    }

    private EntityService getService(String entityKey) {

        switch (entityKey) {
            case "rivTaProfil": return rivTaProfilService;
            case "tjanstekontrakt": return tjanstekontraktService;
            case "tjanstekomponent": return tjanstekomponentService;
            case "vagval": return vagvalService;
            case "logiskAdress": return logiskAdressService;
            case "anropsadress": return anropsAdressService;

            default: throw new IllegalArgumentException();
        }
    }
    // endregion
}