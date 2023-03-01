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
  private static final String MESSAGE_ATTRIBUTE = "message";
  private static final String ERRORS_ATTRIBUTE = "errors";

  private static final String RIVTAPROFIL_KEY = "rivTaProfil";
  private static final String TJANSTEKONTRAKT_KEY = "tjanstekontrakt";
  private static final String TJANSTEKOMPONENT_KEY = "tjanstekomponent";
  private static final String LOGISKADRESS_KEY = "logiskAdress";
  private static final String ANROPSADRESS_KEY = "anropsadress";
  private static final String VAGVAL_KEY = "vagval";
  private static final String ANROPSBEHORIGHET_KEY = "anropsbehorighet";
  private static final String FILTER_KEY = "filter";
  private static final String FILTERCATEGORIZATION_KEY = "filterCategorization";

  private static final String VALID_ENTITIES_REGEX =
          "rivTaProfil|tjanstekontrakt|tjanstekomponent|vagval|logiskAdress|anropsadress|anropsbehorighet|filter|filterCategorization";


  /** Adds custom validation for entity objects */
  @InitBinder("instance")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(entityValidator);
  }

  // region VIEW MODEL MANIPULATION

  /**
   * Populates the model used in List-views with data.
   *
   * @param entity The datatype currently in view.
   * @param model The data model to be manipulated, and then populate the view.
   * @param offset pagination
   * @param max pagination
   * @return The webpage view, with data model.
   */
  @GetMapping("/{entity:" + VALID_ENTITIES_REGEX + "}")
  public String index(
      @PathVariable String entity, Model model,
      @RequestParam(value = "filterFields", required = false) List<String> filterFields,
      @RequestParam(value = "filterConditions", required = false) List<String> filterConditions,
      @RequestParam(value = "filterTexts", required = false) List<String> filterTexts,
      @RequestParam(defaultValue = "0") Integer offset,
      @RequestParam(defaultValue = "10") Integer max,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(required = false) boolean sortDesc) {
    if (entity == null || entity.length() == 0) {
      return "home/index";
    }
    model.addAttribute("entityName", getService(entity).getEntityName());
    List<ListFilter> filters = buildListFilters(filterFields, filterConditions, filterTexts);
    PagedEntityList<?> list = getService(entity).getEntityList(offset, max, filters, sortBy, sortDesc);
    model.addAttribute("list", list);
    model.addAttribute("basePath", "/" + entity);
    return entity + "/list";
  }

  /**
   * Populates the model used in Show-views with data.
   *
   * @param entity The datatype currently in view.
   * @param model The data model to be manipulated, and then populate the view.
   * @param id The specific entity-id of the object to be viewed.
   * @return The webpage view, with data model.
   */
  @GetMapping("/{entity:" + VALID_ENTITIES_REGEX + "}/{id}")
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
   *
   * @param model The data model to be manipulated, and then populate the view.
   * @param entity The datatype currently in view.
   * @return The webpage view, with data model.
   */
  @GetMapping("/{entity:" + VALID_ENTITIES_REGEX + "}/create")
  public String create(Model model, @PathVariable String entity) {
    model.addAttribute("instance", getService(entity).createEntity());
    return prepareCreateView(entity, model);
  }

  /**
   * Populates the model used in Edit-views with data.
   *
   * @param entity The datatype currently in view.
   * @param model The data model to be manipulated, and then populate the view.
   * @param id The specific entity-id of the object to be edited.
   * @return The webpage view, with data model.
   */
  @GetMapping("/{entity:" + VALID_ENTITIES_REGEX + "}/edit/{id}")
  public String edit(@PathVariable String entity, Model model, @PathVariable Long id, RedirectAttributes attributes) {
    Optional instance = getService(entity).findById(id);
    if (!instance.isPresent()) return redirectWithEntityNotFoundError(entity, id, attributes);
    model.addAttribute("instance", instance.get());
    return prepareEditView(entity, model);
  }

  /**
   * Populates the model used in confirm-views used to acknowledge bulk delete.
   *
   * @param entity The datatype currently in view.
   * @param model The data model to be manipulated, and then populate the view.
   * @param toDelete List of id:s to delete.
   * @return The webpage view, with data model.
   */
  @PostMapping("/{entity:" + VALID_ENTITIES_REGEX + "}/confirmDelete")
  public String confirmDelete(@PathVariable String entity, Model model,
                              @RequestParam(value = "toDelete", required = false) List<Long> toDelete) {
    if (entity == null || entity.length() == 0) {
      return "home/index";
    }
    List<AbstractVersionInfo> okToDelete = new ArrayList<>();
    List<AbstractVersionInfo> notToDelete = new ArrayList<>();

    if (toDelete == null) {
      model.addAttribute(ERRORS_ATTRIBUTE, Collections.singletonList("Inget att ta bort"));
    } else {
      for (Long id : toDelete) {
        Optional<AbstractVersionInfo> instance = getService(entity).findById(id);
        if (instance.isPresent() && getService(entity).isUserAllowedToDelete(instance.get(), getUserName())) {
          okToDelete.add(instance.get());
        } else if (instance.isPresent()) {
          notToDelete.add(instance.get());
          model.addAttribute(ERRORS_ATTRIBUTE, Collections.singletonList("Det finns objekt som inte kan tas bort, se nedan."));
        }
      }
    }
    model.addAttribute("okToDelete", okToDelete);
    model.addAttribute("notToDelete", notToDelete);
    model.addAttribute("basePath", "/" + entity);
    model.addAttribute("entityName", getService(entity).getEntityName());
    return "crud/confirm";
  }

  // endregion

  // region CREATION

  // *****************
  // CREATION via POST
  // *****************

  @PostMapping("/rivTaProfil/create")
  public String create(@Valid @ModelAttribute("instance") RivTaProfil instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return create(RIVTAPROFIL_KEY, instance, result, model, attributes);
  }

  @PostMapping("/tjanstekontrakt/create")
  public String create(@Valid @ModelAttribute("instance") Tjanstekontrakt instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return create(TJANSTEKONTRAKT_KEY, instance, result, model, attributes);
  }

  @PostMapping("/tjanstekomponent/create")
  public String create(@Valid @ModelAttribute("instance") Tjanstekomponent instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return create(TJANSTEKOMPONENT_KEY, instance, result, model, attributes);
  }

  @PostMapping("/logiskAdress/create")
  public String create(@Valid @ModelAttribute("instance") LogiskAdress instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return create(LOGISKADRESS_KEY, instance, result, model, attributes);
  }

  @PostMapping("/anropsadress/create")
  public String create(@Valid @ModelAttribute("instance") AnropsAdress instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return create(ANROPSADRESS_KEY, instance, result, model, attributes);
  }

  @PostMapping("/vagval/create")
  public String create(@Valid @ModelAttribute("instance") Vagval instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return create(VAGVAL_KEY, instance, result, model, attributes);
  }

  @PostMapping("/anropsbehorighet/create")
  public String create(@Valid @ModelAttribute("instance") Anropsbehorighet instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return create(ANROPSBEHORIGHET_KEY, instance, result, model, attributes);
  }

  @PostMapping("/filter/create")
  public String create(@Valid @ModelAttribute("instance") Filter instance,
                       @RequestParam("logiskAdress") long logiskAdress,
                       @RequestParam("tjanstekonsument") long tjanstekonsument,
                       @RequestParam("tjanstekontrakt") long tjanstekontrakt,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    // Lookup Anropsbehorighet from the 3 parameters
    Anropsbehorighet ab = anropsBehorighetService.getAnropsbehorighet(logiskAdress, tjanstekonsument, tjanstekontrakt);
    if (ab == null) {
      result.addError(
          new ObjectError("globalError", "Ingen anropsbehörighet som matchar valda värden."));
      return prepareCreateView(FILTER_KEY, model);
    }
    // Uniqueness must be checked after anropsbehorighet has been looked up
    if (filterService.hasDuplicate(instance)) {
      result.addError(new ObjectError("globalError", "Filtret är inte unikt."));
      return prepareCreateView(FILTER_KEY, model);
    }
    instance.setAnropsbehorighet(ab);
    return create(FILTER_KEY, instance, result, model, attributes);
  }

  @PostMapping("/filterCategorization/create")
  public String create(@Valid @ModelAttribute("instance") Filtercategorization instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return create(FILTERCATEGORIZATION_KEY, instance, result, model, attributes);
  }

  // endregion

  // region UPDATE

  // ***************
  // UPDATE via POST
  // ***************

  @PostMapping("/rivTaProfil/update")
  public String update(@Valid @ModelAttribute("instance") RivTaProfil instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return update(RIVTAPROFIL_KEY, instance, result, model, attributes);
  }

  @PostMapping("/tjanstekontrakt/update")
  public String update(@Valid @ModelAttribute("instance") Tjanstekontrakt instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return update(TJANSTEKONTRAKT_KEY, instance, result, model, attributes);
  }

  @PostMapping("/tjanstekomponent/update")
  public String update(@Valid @ModelAttribute("instance") Tjanstekomponent instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return update(TJANSTEKOMPONENT_KEY, instance, result, model, attributes);
  }

  @PostMapping("/logiskAdress/update")
  public String update(@Valid @ModelAttribute("instance") LogiskAdress instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return update(LOGISKADRESS_KEY, instance, result, model, attributes);
  }

  @PostMapping("/anropsadress/update")
  public String update(@Valid @ModelAttribute("instance") AnropsAdress instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return update(ANROPSADRESS_KEY, instance, result, model, attributes);
  }

  @PostMapping("/vagval/update")
  public String update( @Valid @ModelAttribute("instance") Vagval instance,
                        BindingResult result, Model model, RedirectAttributes attributes) {
    return update(VAGVAL_KEY, instance, result, model, attributes);
  }

  @PostMapping("/anropsbehorighet/update")
  public String update(@Valid @ModelAttribute("instance") Anropsbehorighet instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return update(ANROPSBEHORIGHET_KEY, instance, result, model, attributes);
  }

  @PostMapping("/filter/update")
  public String update(@Valid @ModelAttribute("instance") Filter instance,
                       @RequestParam("logiskAdress") long logiskAdress,
                       @RequestParam("tjanstekonsument") long tjanstekonsument,
                       @RequestParam("tjanstekontrakt") long tjanstekontrakt,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    // Lookup Anropsbehorighet from the 3 parameters
    Anropsbehorighet ab =
        anropsBehorighetService.getAnropsbehorighet(
            logiskAdress, tjanstekonsument, tjanstekontrakt);
    if (ab == null) {
      result.addError(
          new ObjectError("globalError", "Ingen anropsbehörighet som matchar valda värden."));
      return prepareEditView(FILTER_KEY, model);
    }
    instance.setAnropsbehorighet(ab);
    // Uniqueness must be checked after anropsbehorighet has been looked up
    if (filterService.hasDuplicate(instance)) {
      result.addError(new ObjectError("globalError", "Filtret är inte unikt."));
      return prepareEditView(FILTER_KEY, model);
    }
    return update(FILTER_KEY, instance, result, model, attributes);
  }

  @PostMapping("/filterCategorization/update")
  public String update(@Valid @ModelAttribute("instance") Filtercategorization instance,
                       BindingResult result, Model model, RedirectAttributes attributes) {
    return update(FILTERCATEGORIZATION_KEY, instance, result, model, attributes);
  }
  // endregion

  // region DELETION

  // *****************
  // DELETION via POST
  // *****************

  @PostMapping("/{entity:" + VALID_ENTITIES_REGEX + "}/delete")
  public String delete(@PathVariable String entity, @RequestParam Long id, RedirectAttributes attributes) {
    try {
      if (getService(entity).delete(id, getUserName())) {
        attributes.addFlashAttribute(MESSAGE_ATTRIBUTE, getService(entity).getEntityName() + " borttagen");
      } else {
        attributes.addFlashAttribute(ERRORS_ATTRIBUTE,
                getService(entity).getEntityName() + " kunde inte tas bort på grund av användning i annan konfiguration");
      }
    } catch (Exception e) {
      String error = "Kunde inte radera. " + e;
      attributes.addFlashAttribute(ERRORS_ATTRIBUTE, error);
      log.error(error, e);
    }
    return redirectToIndex(entity);
  }

  /**
   * Executes bulk delete of selected ids (called from confirm view).
   */
  @PostMapping("/{entity:" + VALID_ENTITIES_REGEX + "}/bulkDelete")
  public String bulkDelete(@PathVariable String entity, RedirectAttributes attributes,
                           @RequestParam(value = "toDelete", required = false) List<Long> toDelete) {
    if (entity == null || entity.length() == 0) {
      return "home/index";
    }

    if (toDelete == null) {
      attributes.addAttribute(ERRORS_ATTRIBUTE, Collections.singletonList("Inget att ta bort"));
      return redirectToIndex(entity);
    }

    int successCounter = 0;
    int failCounter = 0;

    try {
      for (Long id : toDelete) {
        if (getService(entity).delete(id, getUserName())) successCounter++; else failCounter++;
      }
      attributes.addFlashAttribute(MESSAGE_ATTRIBUTE, String.format("Tog bort %d objekt.", successCounter));
      if (failCounter > 0) {
        attributes.addFlashAttribute(ERRORS_ATTRIBUTE,String.format("Misslyckades att ta bort %d objekt.", failCounter));
      }
    } catch (Exception e) {
      String error = "Fel vid bulk-borttagning. " + e;
      attributes.addFlashAttribute(ERRORS_ATTRIBUTE, error);
      log.error(error, e);
    }
    return redirectToIndex(entity);
  }

  // endregion

  // region PRIVATE HELPERS

  // ************************
  // PRIVATE HELPER FUNCTIONS
  // ************************

  private String create(String entity, AbstractVersionInfo instance,
                        BindingResult result, Model model, RedirectAttributes attributes) {
    if (result.hasErrors()) {
      return prepareCreateView(entity, model);
    }
    if (getService(entity).getId(instance) != 0) {
      String error = "Kunde inte skapa instans. Id skall inte anges.";
      attributes.addFlashAttribute(ERRORS_ATTRIBUTE, error);
      return redirectToIndex(entity);
    }
    try {
      getService(entity).add(instance, getUserName());
      attributes.addFlashAttribute(MESSAGE_ATTRIBUTE, getService(entity).getEntityName() + " skapad");
      return redirectToIndex(entity);
    } catch (Exception e) {
      result.addError(new ObjectError("globalError", e.toString()));
      return prepareCreateView(entity, model);
    }
  }

  private String update(String entity, AbstractVersionInfo instance,
                        BindingResult result, Model model, RedirectAttributes attributes) {
    if (result.hasErrors()) {
      return prepareEditView(entity, model);
    }
    if (getService(entity).getId(instance) == 0) {
      String error = "Kunde inte uppdatera. Id saknas.";
      attributes.addFlashAttribute(ERRORS_ATTRIBUTE, error);
      return redirectToIndex(entity);
    }
    try {
      getService(entity).update(instance, getUserName());
      attributes.addFlashAttribute(MESSAGE_ATTRIBUTE, getService(entity).getEntityName() + " uppdaterad");
      return redirectToIndex(entity);
    } catch (ObjectOptimisticLockingFailureException e) {
      String error = "Kunde inte uppdatera. Objektet har ändrats av en annan användare.";
      attributes.addFlashAttribute(ERRORS_ATTRIBUTE, error);
      log.error(error, e);
      return redirectToIndex(entity);
    } catch (Exception e) {
      String error = "Kunde inte uppdatera. " + e;
      attributes.addFlashAttribute(ERRORS_ATTRIBUTE, error);
      log.error(error, e);
      return redirectToIndex(entity);
    }
  }

  private String getUserName() {
    Subject subject = SecurityUtils.getSubject();
    return subject.getPrincipal().toString();
  }

  private EntityService getService(String entityKey) {

    switch (entityKey) {
      case RIVTAPROFIL_KEY:
        return rivTaProfilService;
      case TJANSTEKONTRAKT_KEY:
        return tjanstekontraktService;
      case TJANSTEKOMPONENT_KEY:
        return tjanstekomponentService;
      case VAGVAL_KEY:
        return vagvalService;
      case LOGISKADRESS_KEY:
        return logiskAdressService;
      case ANROPSADRESS_KEY:
        return anropsAdressService;
      case ANROPSBEHORIGHET_KEY:
        return anropsBehorighetService;
      case FILTER_KEY:
        return filterService;
      case FILTERCATEGORIZATION_KEY:
        return filterCategorizationService;

      default:
        throw new IllegalArgumentException();
    }
  }

  private void addFormAttributesToModel(Model model, String entityKey) {
    model.addAttribute("entityName", getService(entityKey).getEntityName());
    model.addAttribute("entityKey", entityKey);
    model.addAttribute("basePath", "/" + entityKey);

    model.addAttribute("rivtaprofil_selectable_options", rivTaProfilService.findAllNotDeleted());
    model.addAttribute(
        "tjanstekontrakt_selectable_options", tjanstekontraktService.findAllNotDeleted());
    model.addAttribute(
        "tjanstekomponent_selectable_options", tjanstekomponentService.findAllNotDeleted());
    model.addAttribute(
        "tjanstekonsument_selectable_options",
        tjanstekomponentService.findAllNotDeleted());
    model.addAttribute("logiskadress_selectable_options", logiskAdressService.findAllNotDeleted());
    model.addAttribute("anropsadress_selectable_options", anropsAdressService.findAllNotDeleted());
    model.addAttribute("filter_selectable_options", filterService.findAllNotDeleted());
  }

  private List<ListFilter> buildListFilters(List<String> filterFields, List<String> filterConditions, List<String> filterTexts) {
    List<ListFilter> list = new ArrayList<>();
    if (filterFields == null || filterConditions == null || filterTexts == null) return list;
    int size = Math.min(Math.min(filterFields.size(), filterConditions.size()), filterTexts.size());
    for (int i = 0; i < size; i++) {
      list.add(new ListFilter(filterFields.get(i), filterConditions.get(i), filterTexts.get(i)));
    }
    return list;
  }

  private String prepareCreateView(String entity, Model model) {
    addFormAttributesToModel(model, entity);
    return "crud/create";
  }

  private String prepareEditView(String entity, Model model) {
    addFormAttributesToModel(model, entity);
    return "crud/edit";
  }

  private String redirectToIndex(String entity) {
    return "redirect:/" + entity;
  }

  private String redirectWithEntityNotFoundError(String entity, Long id, RedirectAttributes attributes) {
    String error = String.format("%s med id %d hittades ej.", getService(entity).getEntityName(), id);
    attributes.addFlashAttribute(ERRORS_ATTRIBUTE, error);
    return redirectToIndex(entity);
  }
  // endregion
}
