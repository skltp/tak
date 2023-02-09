package se.skltp.tak.web.controller;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PublicationVersionController {

  @Autowired PublicationVersionService pubVerService;

  // List data services.
  @Autowired RivTaProfilService rivTaProfilService;
  @Autowired TjanstekontraktService tjanstekontraktService;
  @Autowired TjanstekomponentService tjanstekomponentService;
  @Autowired LogiskAdressService logiskAdressService;
  @Autowired AnropsAdressService anropsAdressService;
  @Autowired VagvalService vagvalService;
  @Autowired AnropsBehorighetService anropsBehorighetService;
  @Autowired FilterService filterService;
  @Autowired FilterCategorizationService filterCategorizationService;

  @RequestMapping("/pubversion")
  public String index(Model model,
                      @RequestParam(defaultValue = "0")  Integer offset,
                      @RequestParam(defaultValue = "10") Integer max) {
    modelBasicPrep(model);

    PagedEntityList list = pubVerService.getEntityList(offset, max);
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

    PublishDataWrapper publishData = new PublishDataWrapper();
    publishData.ScanForEntriesAffectedByPubVer(id);

    // Add found entries to model and push to browser.
    populateModelWithPubVerSubEntries(model, publishData);
    return "pubversion/show";
  }

  /**
   * Get Page Render for the Creation Page
   */
  @GetMapping("/pubversion/create")
  public String save(Model model) {
    modelBasicPrep(model);

    // Add blank pubVer to model.
    model.addAttribute("instance", new PubVersion());

    PublishDataWrapper publishData = new PublishDataWrapper();
    publishData.ScanForPrePublishedEntries();

    boolean publishQualityIsOk = true;

    try {
      QualityAndSanityCheck(publishData);
    } catch (IllegalStateException isE) {
      publishQualityIsOk = false;
    }

    // Add processed entries to model and push to browser.
    populateModelWithPubVerSubEntries(model, publishData);
    // Mark in web model if publish data passed quality check.
    System.out.println("Publishing enabled: " + publishQualityIsOk);
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
                     BindingResult result, ModelMap model, RedirectAttributes attributes) {

    return "pubversion/create";
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
  // Data checks.
  private void QualityAndSanityCheck(PublishDataWrapper publishData) {
    // Check that the current user actually has anything to publish.
    CheckIfUserHasChangesToPublish(publishData);

    // Below section scans all entries and their referenced sub-entries, to ensure that they have the same editor.
    // The below approach is improved from the prior TAK implementation,
    //   wherein this one will scan for and list all such errors,
    //   whereas the prior one would stop and report on first found error.
    // Below code has, just as the prior did, the loop-hole however that if one user makes two consecutive TAK-edits,
    //  then there could still be mistaken mappings, even though the updated-by-username is the same on both entities.
    List<String> errorLines = new ArrayList<>();
    checkAnropsAdressReferences(publishData, errorLines);
    checkVagvalReferences(publishData, errorLines);
    checkAnropsbehorighetReferences(publishData, errorLines);
    checkFilterCategorizationReferences(publishData, errorLines);
    
    if (errorLines.size() > 0) {
      String excMsg = "There were mismatches in editing users between previewed data entries and their sub-entries. List below:\n" + 
          String.join("\n", errorLines);
      throw new IllegalStateException (excMsg);
    }
  }

  private void CheckIfUserHasChangesToPublish(PublishDataWrapper publishData) {
    if (publishData.UsernameHasNoEntryAmongData("username")) {
      throw new IllegalStateException("Quality check before allowing publishing to occur found that current user has no pending items to publish.");
    }
  }

  private void checkAnropsAdressReferences(PublishDataWrapper publishData, List<String> errorLines) {
    for (AnropsAdress adr : publishData.anropsAdressList) {
      CheckMatchingEditors(adr, adr.getTjanstekomponent(), errorLines);
      CheckMatchingEditors(adr, adr.getRivTaProfil(), errorLines);
    }
  }
  private void checkVagvalReferences(PublishDataWrapper publishData, List<String> errorLines) {
    for (Vagval vv : publishData.vagvalList) {
      CheckMatchingEditors(vv, vv.getTjanstekontrakt(), errorLines);
      CheckMatchingEditors(vv, vv.getLogiskAdress(), errorLines);
      CheckMatchingEditors(vv, vv.getAnropsAdress(), errorLines);
    }
  }
  private void checkAnropsbehorighetReferences(PublishDataWrapper publishData, List<String> errorLines) {
    for (Anropsbehorighet ab : publishData.anropsbehorighetList) {
      CheckMatchingEditors(ab, ab.getTjanstekontrakt(), errorLines);
      CheckMatchingEditors(ab, ab.getLogiskAdress(), errorLines);
      CheckMatchingEditors(ab, ab.getTjanstekonsument(), errorLines);
    }
  }
  private void checkFilterCategorizationReferences(PublishDataWrapper publishData, List<String> errorLines) {
    for (Filtercategorization fc : publishData.filtercategorizationList) {
      CheckMatchingEditors(fc, fc.getFilter(), errorLines);
    }
  }

  // Type combos, to extract pretty-printed error meta.
  private void CheckMatchingEditors(AnropsAdress aa, Tjanstekomponent tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(aa, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, aa.getUpdatedBy(), tk.getUpdatedBy(),
          "Anropsadress:" + aa.getAdress(), "Tjänstekonsument:" + tk.getHsaId());
    }
  }
  private void CheckMatchingEditors(AnropsAdress aa, RivTaProfil rp, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(aa, rp)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, aa.getUpdatedBy(), rp.getUpdatedBy(),
          "Anropsadress:" + aa.getAdress(), "RivTaProfil:" + rp.getNamn());
    }
  }
  private void CheckMatchingEditors(Vagval vv, Tjanstekontrakt tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(vv, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, vv.getUpdatedBy(), tk.getUpdatedBy(),
          "Vägval:" + vv.getId(), "Tjänstekontrakt:" + tk.getNamnrymd());
    }
  }
  private void CheckMatchingEditors(Vagval vv, LogiskAdress la, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(vv, la)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, vv.getUpdatedBy(), la.getUpdatedBy(),
          "Vägval:" + vv.getId(), "LogiskAdress:" + la.getHsaId());
    }
  }
  private void CheckMatchingEditors(Vagval vv, AnropsAdress aa, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(vv, aa)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, vv.getUpdatedBy(), aa.getUpdatedBy(),
          "Vägval:" + vv.getId(), "AnropsAdress:" + aa.getAdress());
    }
  }

  private void CheckMatchingEditors(Anropsbehorighet ab, Tjanstekontrakt tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(ab, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, ab.getUpdatedBy(), tk.getUpdatedBy(),
          "Anropsbehorighet:" + ab.getIntegrationsavtal(), "Tjänstekontrakt:" + tk.getNamnrymd());
    }
  }
  private void CheckMatchingEditors(Anropsbehorighet ab, LogiskAdress tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(ab, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, ab.getUpdatedBy(), tk.getUpdatedBy(),
          "Anropsbehorighet:" + ab.getIntegrationsavtal(), "LogiskAdress:" + tk.getHsaId());
    }
  }
  
  private void CheckMatchingEditors(Anropsbehorighet ab, Tjanstekomponent tk, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(ab, tk)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, ab.getUpdatedBy(), tk.getUpdatedBy(),
          "Anropsbehorighet:" + ab.getIntegrationsavtal(), "Tjänstekomponent:" + tk.getHsaId());
    }
  }
  private void CheckMatchingEditors(Filtercategorization fc, Filter f, List<String> errorLines) {
    if (AbstractVersionInfoEditorsMismatch(fc, f)) {
      RecordErrorWhenEntityEditorsMismatch(
          errorLines, fc.getUpdatedBy(), f.getUpdatedBy(),
          "FilterKategorisering:" + fc.getCategory(), "Filter:" + f.getServicedomain());
    }
  }
  
  
  private boolean AbstractVersionInfoEditorsMismatch(AbstractVersionInfo avi1,
                                                     AbstractVersionInfo avi2) {
    String publishingUser = avi1.getUpdatedBy();
    String otherEntityUser = avi2.getUpdatedBy();

    return publishingUser != null && otherEntityUser != null
        && !publishingUser.equalsIgnoreCase(otherEntityUser);
  }

  // Throwers
  private void RecordErrorWhenEntityEditorsMismatch(List<String> errorLines,
                                                    String entityUser, String otherEntityUser,
                                                    String entityPrint, String otherEntityPrint) {
    String output =
        "ERROR: Mismatch users:" +
        " Entity User: " + entityUser +
        " Sub-entity user: " + otherEntityUser +
        " Entity details: " + entityPrint +
        " Sub-entity details: " + otherEntityPrint;

    errorLines.add(output);
  }

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

  // Used for data crunching and handling during checks and model pushes.
  private class PublishDataWrapper {
    
    List<AnropsAdress> anropsAdressList;
    List<Anropsbehorighet> anropsbehorighetList;
    List<Filtercategorization> filtercategorizationList;
    List<Filter> filterList;
    List<LogiskAdress> logiskAdressList;
    List<RivTaProfil> rivTaProfilList;
    List<Tjanstekomponent> tjanstekomponentList;
    List<Tjanstekontrakt > tjanstekontraktList;
    List<Vagval> vagvalList;

    PublishDataWrapper() {}

    void ScanForPrePublishedEntries() {
      
      this.anropsAdressList = anropsAdressService.findAllByUpdatedByIsNotNull();
      this.anropsbehorighetList = anropsBehorighetService.findAllByUpdatedByIsNotNull();
      this.filtercategorizationList = filterCategorizationService.findAllByUpdatedByIsNotNull();
      this.filterList = filterService.findAllByUpdatedByIsNotNull();
      this.logiskAdressList = logiskAdressService.findAllByUpdatedByIsNotNull();
      this.rivTaProfilList = rivTaProfilService.findAllByUpdatedByIsNotNull();
      this.tjanstekomponentList = tjanstekomponentService.findAllByUpdatedByIsNotNull();
      this.tjanstekontraktList =  tjanstekontraktService.findAllByUpdatedByIsNotNull();
      this.vagvalList = vagvalService.findAllByUpdatedByIsNotNull();
    }

    void ScanForEntriesAffectedByPubVer(Long id) {
      this.rivTaProfilList = rivTaProfilService.findAllByPubVersion(id);
      this.anropsbehorighetList = anropsBehorighetService.findAllByPubVersion(id);
      this.filtercategorizationList = filterCategorizationService.findAllByPubVersion(id);
      this.filterList = filterService.findAllByPubVersion(id);
      this.logiskAdressList = logiskAdressService.findAllByPubVersion(id);
      this.rivTaProfilList = rivTaProfilService.findAllByPubVersion(id);
      this.tjanstekomponentList = tjanstekomponentService.findAllByPubVersion(id);
      this.tjanstekontraktList =  tjanstekontraktService.findAllByPubVersion(id);
      this.vagvalList = vagvalService.findAllByPubVersion(id);
    }

    public boolean UsernameHasNoEntryAmongData(String loggedInUser) {
      for (AnropsAdress entry: anropsAdressList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
      for (Anropsbehorighet entry: anropsbehorighetList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
      for (Filtercategorization entry: filtercategorizationList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
      for (Filter entry: filterList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
      for (LogiskAdress entry: logiskAdressList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
      for (RivTaProfil entry: rivTaProfilList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
      for (Tjanstekomponent entry: tjanstekomponentList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
      for (Tjanstekontrakt entry: tjanstekontraktList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;
      for (Vagval entry: vagvalList) if (entry.getUpdatedBy().equalsIgnoreCase(loggedInUser)) return false;

      // Else, fallback, found no matches.
      return true;
    }
  }
}
