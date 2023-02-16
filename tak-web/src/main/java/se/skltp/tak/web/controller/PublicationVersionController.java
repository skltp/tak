package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.*;
import se.skltp.tak.web.util.PublishDataWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class PublicationVersionController {

  @Autowired PublicationVersionService pubVerService;
  
  @Autowired AlerterService alerterService;

  private static final Logger log = LoggerFactory.getLogger(PublicationVersionController.class);

  @RequestMapping("/pubversion")
  public String index(Model model,
                      @RequestParam(defaultValue = "0")  Integer offset,
                      @RequestParam(defaultValue = "10") Integer max) {
    modelBasicPrep(model);

    PagedEntityList<PubVersion> list = pubVerService.getEntityList(offset, max);
    model.addAttribute("list", list);

    return "pubversion/list";
  }

  @RequestMapping("/pubversion/{id}")
  public String show(Model model,
                     @PathVariable Long id) {
    //checkAdministratorRole(); // Admin?
    modelBasicPrep(model);

    Optional<PubVersion> instance = pubVerService.findById(id);
    if (!instance.isPresent()) throw new IllegalArgumentException("Entity not found");
    model.addAttribute("instance", instance.get());

    PublishDataWrapper publishData = pubVerService.ScanForEntriesAffectedByPubVer(id);

    // Add found entries to model and push to browser.
    populateModelWithPubVerSubEntries(model, publishData);
    return "pubversion/show";
  }

  /**
   * Get Page Render for the Creation Page
   */
  @GetMapping("/pubversion/create")
  public String create(Model model, HttpServletRequest request) {
    modelBasicPrep(model);

    model.addAttribute("message", alerterService.getMailAlertStatusMessage());

    // Add blank pubVer to model.
    model.addAttribute("instance", new PubVersion());

    PublishDataWrapper publishData = pubVerService.ScanForPrePublishedEntries();

    boolean publishQualityIsOk = true;
    String username = request.getSession().getAttribute("username").toString();
    //String username = SecurityUtils.getSubject().getPrincipal().toString(); // Might also work.

    try {
      publishData.QualityAndSanityCheck(username);
    } catch (IllegalStateException isE) {
      // System.out.print("Publish Preview quality check failed: " +
      //    "Exception content:\n" + isE.getMessage());
      publishQualityIsOk = false;

      // TODO: Log Exception details.
    }

    // Add processed entries to model and push to browser.
    populateModelWithPubVerSubEntries(model, publishData);

    // Mark in web model if publish data passed quality check.
    // TODO: Log Quality check.
    model.addAttribute("enablePublish", publishQualityIsOk);

    return "pubversion/create";
  }

  /**
   * Get Page Render for the Edit Page
   */
  @GetMapping("/pubversion/edit/{id}")
  public String edit(Model model, @PathVariable Long id) {
    modelBasicPrep(model);

    // Find and add pubVer to model.
    Optional instance = pubVerService.findById(id);
    if (!instance.isPresent()) throw new IllegalArgumentException("Entity not found");
    model.addAttribute("instance", instance.get());

    return "pubversion/edit";
  }

  /**
   * Entity Creation pipeline.
   * @return page render.
   */
  @PostMapping("/pubversion/create")
  public String save(@Valid @ModelAttribute("instance") PubVersion instance,
                     HttpServletRequest request,
                     BindingResult result,
                     ModelMap model,
                     RedirectAttributes attributes) {

    log.info("Request to create a PubVersion received.");

    if (result.hasErrors()) {
      return "pubversion/create";
    }

    checkAdministratorRole(); // Gotta stay safe. :3
    String username = request.getSession().getAttribute("username").toString();
    log.info("User " + username + " has passed the Admin check.");

    try {
      PubVersion updatedPV = pubVerService.add(instance, username);
      alerterService.alertOnPublicering(updatedPV);

      System.out.println("POST-SAVE!");
      System.out.println("PUBLICATION COMMENT: " + instance.getKommentar());
      attributes.addFlashAttribute("message", "Publicering skapad.");
      return "redirect:/pubversion";

    } catch (Exception ex) {
      result.addError(new ObjectError("globalError", ex.toString()));
      return "pubversion/create";
    }
  }

  /**
   * Entity Update pipeline.
   * @return page render.
   */
  public String update(@Valid @ModelAttribute("instance") PubVersion instance,
                       BindingResult result, RedirectAttributes attributes) {
    if (result.hasErrors()) {
      return "pubversion/edit";
    }
    try {
      PubVersion newInstance = pubVerService.update(instance);
      attributes.addFlashAttribute("message", "Användare uppdaterad");
      return "redirect:/pubversion";
    }
    catch (Exception e) {
      result.addError(new ObjectError("globalError", e.toString()));
      return "pubversion/edit";
    }
  }

  // Privates.

  // Web View Model crunchers
  private static void modelBasicPrep(Model model) {
    model.addAttribute("entityName", "PubVersion");
    model.addAttribute("basePath", "/pubversion");
  }

  private void populateModelWithPubVerSubEntries(Model model, PublishDataWrapper publishData) {
    model.addAttribute("anropsAdress_pubVerChanges", publishData.anropsAdressList);
    model.addAttribute("anropsBehorighet_pubVerChanges", publishData.anropsbehorighetList);
    model.addAttribute("filter_pubVerChanges", publishData.filterList);
    model.addAttribute("filterCategorization_pubVerChanges", publishData.filtercategorizationList);
    model.addAttribute("logiskAdress_pubVerChanges", publishData.logiskAdressList);
    model.addAttribute("rivTaProfil_pubVerChanges", publishData.rivTaProfilList);
    model.addAttribute("tjanstekomponent_pubVerChanges", publishData.tjanstekomponentList);
    model.addAttribute("tjanstekontrakt_pubVerChanges", publishData.tjanstekontraktList);
    model.addAttribute("vagval_pubVerChanges", publishData.vagvalList);
  }

  private void checkAdministratorRole() {
    if(!SecurityUtils.getSubject().hasRole("Administrator")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }
}
