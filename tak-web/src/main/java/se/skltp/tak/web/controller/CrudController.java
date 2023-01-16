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
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.*;

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


    // region VIEW MODEL MANIPULATION

    /**
     * Populates the model used in List-views with data.
     * @param entity The datatype currently in view.
     * @param model The data model to be manipulated, and then populate the view.
     * @param offset pagination
     * @param max pagination
     * @return The webpage view, with data model.
     */
    @GetMapping("/{entity}")
    public String index(@PathVariable String entity,
                        Model model,
                        @RequestParam(value = "filterField[]", required = false) List<String> filterFields,
                        @RequestParam(value = "filterCondition[]", required = false) List<String> filterConditions,
                        @RequestParam(value = "filterText[]", required = false) List<String> filterTexts,
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
    @GetMapping("/{entity}/{id}")
    public String show(@PathVariable String entity, Model model, @PathVariable Long id) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) throw new IllegalArgumentException("Entity not found");
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
    @GetMapping("/{entity}/create")
    public String create(Model model, @PathVariable String entity) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        model.addAttribute("instance", getService(entity).createEntity());
        model.addAttribute("basePath", "/" + entity);
        populateModelwithSelectionLists(model);
        return entity + "/create";
    }

    /**
     * Populates the model used in Edit-views with data.
     * @param entity The datatype currently in view.
     * @param model The data model to be manipulated, and then populate the view.
     * @param id The specific entity-id of the object to be edited.
     * @return The webpage view, with data model.
     */
    @GetMapping("/{entity}/edit/{id}")
    public String edit(@PathVariable String entity, Model model, @PathVariable Long id) {
        model.addAttribute("entityName", getService(entity).getEntityName());
        Optional instance = getService(entity).findById(id);
        if (!instance.isPresent()) throw new IllegalArgumentException("Entity not found");
        model.addAttribute("instance", instance.get());
        model.addAttribute("basePath", "/" + entity);
        populateModelwithSelectionLists(model);
        return entity + "/edit";
    }

    // endregion

    // region CREATION

    // *****************
    // CREATION via POST
    // *****************

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

    @PostMapping("/anropsbehorighet/create")
    public String save(@Valid @ModelAttribute("instance")Anropsbehorighet instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("anropsbehorighet", instance, result, attributes);
    }

    @PostMapping("/filter/create")
    public String save(@Valid @ModelAttribute("instance") Filter instance,
                       @RequestParam("logiskAdress")long logiskAdress,
                       @RequestParam("tjanstekonsument")long tjanstekonsument,
                       @RequestParam("tjanstekontrakt")long tjanstekontrakt,
                       BindingResult result, RedirectAttributes attributes) {
        // Lookup Anropsbehorighet from the 3 parameters
        Anropsbehorighet ab = anropsBehorighetService.getAnropsbehorighet(logiskAdress, tjanstekonsument, tjanstekontrakt);
        if (ab == null) {
            result.addError(new ObjectError("globalError", "Ingen anropsbehörighet som matchar valda värden."));
            return "filter/create";
        }
        instance.setAnropsbehorighet(ab);
        return save("filter", instance, result, attributes);
    }

    @PostMapping("/filterCategorization/create")
    public String save(@Valid @ModelAttribute("instance")Filtercategorization instance,
                       BindingResult result, ModelMap model, RedirectAttributes attributes) {
        return save("filterCategorization", instance, result, attributes);
    }

    // endregion


    // region UPDATE

    // ***************
    // UPDATE via POST
    // ***************

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

    @PostMapping("/anropsbehorighet/update")
    public String update(@Valid @ModelAttribute("instance") Anropsbehorighet instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("anropsbehorighet", instance, result, attributes);
    }

    @PostMapping("/filter/update")
    public String update(@Valid @ModelAttribute("instance") Filter instance,
                         @RequestParam("logiskAdress")long logiskAdress,
                         @RequestParam("tjanstekonsument")long tjanstekonsument,
                         @RequestParam("tjanstekontrakt")long tjanstekontrakt,
                         BindingResult result, RedirectAttributes attributes) {
        // Lookup Anropsbehorighet from the 3 parameters
        Anropsbehorighet ab = anropsBehorighetService.getAnropsbehorighet(logiskAdress, tjanstekonsument, tjanstekontrakt);
        if (ab == null) {
            result.addError(new ObjectError("globalError", "Ingen anropsbehörighet som matchar valda värden."));
            return "filter/edit";
        }
        instance.setAnropsbehorighet(ab);
        return update("filter", instance, result, attributes);
    }

    @PostMapping("/filterCategorization/update")
    public String update(@Valid @ModelAttribute("instance") Filtercategorization instance,
                         BindingResult result, RedirectAttributes attributes) {
        return update("filterCategorization", instance, result, attributes);
    }
    // endregion

    // region DELETION

    // *****************
    // DELETION via POST
    // *****************

    @PostMapping("/{entity}/delete")
    public String delete(@PathVariable String entity, @RequestParam Long id, RedirectAttributes attributes) {
        if (getService(entity).delete(id, "User")) {
            attributes.addFlashAttribute("message", entity + " borttagen");
        }
        else {
            throw new IllegalArgumentException("Entity not found");
        }
        return "redirect:/" + entity;
    }
    // endregion

    // region PRIVATE HELPERS

    // ************************
    // PRIVATE HELPER FUNCTIONS
    // ************************

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
            case "anropsbehorighet": return anropsBehorighetService;
            case "filter": return filterService;
            case "filterCategorization": return filterCategorizationService;

            default: throw new IllegalArgumentException();
        }
    }

    private void populateModelwithSelectionLists(Model model) {

        List<String> options = new ArrayList<>();
        options.add("option 1");
        options.add("option 2");
        options.add("option 3");
        model.addAttribute("options", options);

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
    // endregion
}