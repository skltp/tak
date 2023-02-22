package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.*;
import se.skltp.tak.web.validator.EntityValidator;

import javax.validation.Valid;
import java.util.*;

@Controller
public class CrudController {

    @Autowired RivTaProfilService rivTaProfilService;
    @Autowired TjanstekontraktService tjanstekontraktService;
    @Autowired TjanstekomponentService tjanstekomponentService;
    @Autowired LogiskAdressService logiskAdressService;
    @Autowired AnropsAdressService anropsAdressService;
    @Autowired VagvalService vagvalService;
    @Autowired AnropsBehorighetService anropsBehorighetService;
    @Autowired FilterService filterService;
    @Autowired FilterCategorizationService filterCategorizationService;
    @Autowired EntityValidator entityValidator;

    private static final Logger log = LoggerFactory.getLogger(CrudController.class);
    private static final String MESSAGE_FLASH_ATTRIBUTE = "message";
    private static final String ERRORS_FLASH_ATTRIBUTE = "errors";
    private static final String VALID_ENTITIES_REGEX =
            "rivTaProfil|tjanstekontrakt|tjanstekomponent|vagval|logiskAdress|anropsadress|anropsbehorighet|filter|filterCategorization";

    /**
     * Adds custom validation for entity objects
     */
    @InitBinder("instance")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(entityValidator);
    }

    // region VIEW MODEL MANIPULATION

    /**
     * Populates the model used in List-views with data.
     * @param entity The datatype currently in view.
     * @param model The data model to be manipulated, and then populate the view.
     * @param offset pagination
     * @param max pagination
     * @return The webpage view, with data model.
     */
    @GetMapping("/{entity:"+ VALID_ENTITIES_REGEX + "}")
    public String index(@PathVariable String entity,
                        Model model,
                        @RequestParam(value = "filterFields", required = false) List<String> filterFields,
                        @RequestParam(value = "filterConditions", required = false) List<String> filterConditions,
                        @RequestParam(value = "filterTexts", required = false) List<String> filterTexts,
                        @RequestParam(defaultValue = "0") Integer offset,
                        @RequestParam(defaultValue = "10") Integer max) {
        if (entity == null || entity.length() == 0 ) {
            return "home/index";
        }
        model.addAttribute("entityName", getService(entity).getEntityName());
        List<ListFilter> filters = buildListFilters(filterFields, filterConditions, filterTexts);
        PagedEntityList list = getService(entity).getEntityList(offset, max, filters);
        model.addAttribute("list", list);
        model.addAttribute("basePath", "/" + entity);
        return entity + "/list";
    }

    /**
     * Populates the model used in Show-views with data.
     * @param entity The datatype currently in view.
     * @param model The data model to be manipulated, and then populate the view.
     * @param id The specific entity-id of the object to be viewed.
     * @return The webpage view, with data model.
     */
    @GetMapping("/{entity:"+ VALID_ENTITIES_REGEX + "}/{id}")
    public String show(@PathVariable String entity, Model model, @PathVariable Long id, RedirectAttributes attributes) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) return redirectWithEntityNotFoundError(entity, id, attributes);
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/" + entity);
        return entity + "/show";
    }

    /**
     * Populates the model used in creation webpages with data.
     * @param model The data model to be manipulated, and then populate the view.
     * @param entity The datatype currently in view.
     * @return The webpage view, with data model.
     */
    @GetMapping("/{entity:"+ VALID_ENTITIES_REGEX + "}/create")
    public String create(Model model, @PathVariable String entity) {
        model.addAttribute("instance", getService(entity).createEntity());
        addFormAttributesToModel(model, entity);
        return entity + "/create";
    }

    /**
     * Populates the model used in Edit-views with data.
     * @param entity The datatype currently in view.
     * @param model The data model to be manipulated, and then populate the view.
     * @param id The specific entity-id of the object to be edited.
     * @return The webpage view, with data model.
     */
    @GetMapping("/{entity:"+ VALID_ENTITIES_REGEX + "}/edit/{id}")
    public String edit(@PathVariable String entity, Model model, @PathVariable Long id, RedirectAttributes attributes) {
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) return redirectWithEntityNotFoundError(entity, id, attributes);
        model.addAttribute("instance", instance.get());
        addFormAttributesToModel(model, entity);
        return entity + "/edit";
    }

    // endregion

    // region CREATION

    // *****************
    // CREATION via POST
    // *****************

    @PostMapping("/rivTaProfil/create")
    public String create(@Valid @ModelAttribute("instance")RivTaProfil instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        return create("rivTaProfil", instance, result, model, attributes);
    }

    @PostMapping("/tjanstekontrakt/create")
    public String create(@Valid @ModelAttribute("instance")Tjanstekontrakt instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        return create("tjanstekontrakt", instance, result, model, attributes);
    }

    @PostMapping("/tjanstekomponent/create")
    public String create(@Valid @ModelAttribute("instance")Tjanstekomponent instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        return create("tjanstekomponent", instance, result, model, attributes);
    }

    @PostMapping("/logiskAdress/create")
    public String create(@Valid @ModelAttribute("instance")LogiskAdress instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        return create("logiskAdress", instance, result, model, attributes);
    }

    @PostMapping("/anropsadress/create")
    public String create(@Valid @ModelAttribute("instance")AnropsAdress instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        return create("anropsadress", instance, result, model, attributes);
    }

    @PostMapping("/vagval/create")
    public String create(@Valid @ModelAttribute("instance")Vagval instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        return create("vagval", instance, result, model, attributes);
    }

    @PostMapping("/anropsbehorighet/create")
    public String create(@Valid @ModelAttribute("instance")Anropsbehorighet instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        return create("anropsbehorighet", instance, result, model, attributes);
    }

    @PostMapping("/filter/create")
    public String create(@Valid @ModelAttribute("instance") Filter instance,
                       @RequestParam("logiskAdress")long logiskAdress,
                       @RequestParam("tjanstekonsument")long tjanstekonsument,
                       @RequestParam("tjanstekontrakt")long tjanstekontrakt,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        // Lookup Anropsbehorighet from the 3 parameters
        Anropsbehorighet ab = anropsBehorighetService.getAnropsbehorighet(logiskAdress, tjanstekonsument, tjanstekontrakt);
        if (ab == null) {
            result.addError(new ObjectError("globalError", "Ingen anropsbehörighet som matchar valda värden."));
            addFormAttributesToModel(model, "filter");
            return "filter/create";
        }
        // Uniqueness must be checked after anropsbehorighet has been looked up
        if (filterService.hasDuplicate(instance)) {
            result.addError(new ObjectError("globalError", "Filtret är inte unikt."));
            addFormAttributesToModel(model,"filter");
            return "filter/create";
        }
        instance.setAnropsbehorighet(ab);
        return create("filter", instance, result, model, attributes);
    }

    @PostMapping("/filterCategorization/create")
    public String create(@Valid @ModelAttribute("instance")Filtercategorization instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
        return create("filterCategorization", instance, result, model, attributes);
    }

    // endregion


    // region UPDATE

    // ***************
    // UPDATE via POST
    // ***************

    @PostMapping("/rivTaProfil/update")
    public String update(@Valid @ModelAttribute("instance")RivTaProfil instance,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        return update("rivTaProfil", instance, result, model, attributes);
    }

    @PostMapping("/tjanstekontrakt/update")
    public String update(@Valid @ModelAttribute("instance")Tjanstekontrakt instance,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        return update("tjanstekontrakt", instance, result, model, attributes);
    }

    @PostMapping("/tjanstekomponent/update")
    public String update(@Valid @ModelAttribute("instance") Tjanstekomponent instance,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        return update("tjanstekomponent", instance, result, model, attributes);
    }

    @PostMapping("/logiskAdress/update")
    public String update(@Valid @ModelAttribute("instance") LogiskAdress instance,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        return update("logiskAdress", instance, result, model, attributes);
    }

    @PostMapping("/anropsadress/update")
    public String update(@Valid @ModelAttribute("instance") AnropsAdress instance,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        return update("anropsadress", instance, result, model, attributes);
    }

    @PostMapping("/vagval/update")
    public String update(@Valid @ModelAttribute("instance") Vagval instance,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        return update("vagval", instance, result, model, attributes);
    }

    @PostMapping("/anropsbehorighet/update")
    public String update(@Valid @ModelAttribute("instance") Anropsbehorighet instance,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        return update("anropsbehorighet", instance, result, model, attributes);
    }

    @PostMapping("/filter/update")
    public String update(@Valid @ModelAttribute("instance") Filter instance,
                         @RequestParam("logiskAdress")long logiskAdress,
                         @RequestParam("tjanstekonsument")long tjanstekonsument,
                         @RequestParam("tjanstekontrakt")long tjanstekontrakt,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        // Lookup Anropsbehorighet from the 3 parameters
        Anropsbehorighet ab = anropsBehorighetService.getAnropsbehorighet(logiskAdress, tjanstekonsument, tjanstekontrakt);
        if (ab == null) {
            result.addError(new ObjectError("globalError", "Ingen anropsbehörighet som matchar valda värden."));
            addFormAttributesToModel(model, "filter");
            return "filter/edit";
        }
        instance.setAnropsbehorighet(ab);
        // Uniqueness must be checked after anropsbehorighet has been looked up
        if (filterService.hasDuplicate(instance)) {
            result.addError(new ObjectError("globalError", "Filtret är inte unikt."));
            addFormAttributesToModel(model, "filter");
            return "filter/edit";
        }
        return update("filter", instance, result, model, attributes);
    }

    @PostMapping("/filterCategorization/update")
    public String update(@Valid @ModelAttribute("instance") Filtercategorization instance,
                         BindingResult result, Model model, RedirectAttributes attributes) {
        return update("filterCategorization", instance, result, model, attributes);
    }
    // endregion

    // region DELETION

    // *****************
    // DELETION via POST
    // *****************

    @PostMapping("/{entity:"+ VALID_ENTITIES_REGEX + "}/delete")
    public String delete(@PathVariable String entity, @RequestParam Long id, RedirectAttributes attributes) {
        try {
            if (getService(entity).delete(id, getUserName())) {
                attributes.addFlashAttribute(MESSAGE_FLASH_ATTRIBUTE,
                        getService(entity).getEntityName() + " borttagen");
            }
            else {
                attributes.addFlashAttribute(ERRORS_FLASH_ATTRIBUTE,
                        getService(entity).getEntityName() + " kunde inte tas bort på grund av användning i annan konfiguration");
            }
        }
        catch (Exception e) {
            String error = "Kunde inte radera. " + e;
            attributes.addFlashAttribute(ERRORS_FLASH_ATTRIBUTE, error);
            log.error(error, e);
        }
        return "redirect:/" + entity;
    }
    // endregion

    // region PRIVATE HELPERS

    // ************************
    // PRIVATE HELPER FUNCTIONS
    // ************************

    private String create(String entity, AbstractVersionInfo instance,
                          BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            addFormAttributesToModel(model, entity);
            return entity + "/create";
        }
        if (getService(entity).getId(instance) != 0) {
            String error = "Kunde inte skapa instans. Id skall inte anges.";
            attributes.addFlashAttribute(ERRORS_FLASH_ATTRIBUTE, error);
            return "redirect:/" + entity;
        }
        try {
            AbstractVersionInfo newInstance = getService(entity).add(instance, getUserName());
            attributes.addFlashAttribute(MESSAGE_FLASH_ATTRIBUTE, getService(entity).getEntityName() + " skapad");
            return "redirect:/" + entity;
        }
        catch (Exception e) {
            result.addError(new ObjectError("globalError", e.toString()));
            addFormAttributesToModel(model, entity);
            return entity + "/create";
        }
    }

    private String update(String entity, AbstractVersionInfo instance,
                          BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            addFormAttributesToModel(model, entity);
            return entity + "/edit";
        }
        if (getService(entity).getId(instance) == 0) {
            String error = "Kunde inte uppdatera. Id saknas.";
            attributes.addFlashAttribute(ERRORS_FLASH_ATTRIBUTE, error);
            return "redirect:/" + entity;
        }
        try {
            AbstractVersionInfo newInstance = getService(entity).update(instance, getUserName());
            attributes.addFlashAttribute(MESSAGE_FLASH_ATTRIBUTE, getService(entity).getEntityName() + " uppdaterad");
            return "redirect:/" + entity;
        }
        catch (ObjectOptimisticLockingFailureException e) {
            String error = "Kunde inte uppdatera. Objektet har ändrats av en annan användare.";
            attributes.addFlashAttribute(ERRORS_FLASH_ATTRIBUTE, error);
            log.error(error, e);
            return "redirect:/" + entity;
        }
        catch (Exception e) {
            String error = "Kunde inte uppdatera. " + e;
            attributes.addFlashAttribute(ERRORS_FLASH_ATTRIBUTE, error);
            log.error(error, e);
            return "redirect:/" + entity;
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
            case "anropsbehorighet": return anropsBehorighetService;
            case "filter": return filterService;
            case "filterCategorization": return filterCategorizationService;

            default: throw new IllegalArgumentException();
        }
    }

    private void addFormAttributesToModel(Model model, String entityKey) {
        model.addAttribute("entityName", getService(entityKey).getEntityName());
        model.addAttribute("basePath", "/" + entityKey);

        model.addAttribute("rivtaprofil_selectable_options", rivTaProfilService.findAllNotDeleted());
        model.addAttribute("tjanstekontrakt_selectable_options", tjanstekontraktService.findAllNotDeleted());
        model.addAttribute("tjanstekomponent_selectable_options", tjanstekomponentService.findAllNotDeleted());
        model.addAttribute("tjanstekonsument_selectable_options", tjanstekomponentService.findAllNotDeleted()); // TODO: FILTER ONLY KONSUMENTER?
        model.addAttribute("logiskadress_selectable_options", logiskAdressService.findAllNotDeleted());
        model.addAttribute("anropsadress_selectable_options", anropsAdressService.findAllNotDeleted());
        model.addAttribute("filter_selectable_options", filterService.findAllNotDeleted());
    }

    private List<ListFilter> buildListFilters(List<String> filterFields,
                                              List<String> filterConditions,
                                              List<String> filterTexts) {
        List<ListFilter> list = new ArrayList<>();
        if (filterFields == null || filterConditions == null || filterTexts == null) return list;
        int size = Math.min(Math.min(filterFields.size(), filterConditions.size()), filterTexts.size());
        for (int i = 0; i < size; i++) {
            list.add(new ListFilter(filterFields.get(i), filterConditions.get(i), filterTexts.get(i)));
        }
        return list;
    }

    private String redirectWithEntityNotFoundError(String entity, Long id, RedirectAttributes attributes) {
        String error = String.format("%s med id %d hittades ej.", getService(entity).getEntityName(), id);
        attributes.addFlashAttribute(ERRORS_FLASH_ATTRIBUTE, error);
        return "redirect:/" + entity;
    }
    // endregion
}