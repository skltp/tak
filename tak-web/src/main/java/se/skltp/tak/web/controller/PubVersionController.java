package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
import se.skltp.tak.web.entity.Locktb;
import se.skltp.tak.web.service.*;
import se.skltp.tak.web.util.PublishDataWrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

@Controller
public class PubVersionController {

  @Autowired PubVersionService pubVersionService;
  @Autowired LockService lockService;
  @Autowired AlerterService alerterService;

  private static final Logger log = LoggerFactory.getLogger(PubVersionController.class);

  @RequestMapping("/pubversion")
  public String index(Model model,
                      @RequestParam(defaultValue = "0")  Integer offset,
                      @RequestParam(defaultValue = "10") Integer max) {
    modelBasicPrep(model);

    PagedEntityList<PubVersion> list = pubVersionService.getEntityList(offset, max);
    model.addAttribute("list", list);

    return "pubversion/list";
  }

  @RequestMapping("/pubversion/{id}")
  public String show(Model model,
                     @PathVariable Long id) {
    //checkAdministratorRole(); // Admin?
    modelBasicPrep(model);

    PubVersion instance = pubVersionService.findById(id);
    model.addAttribute("instance", instance);

    PublishDataWrapper publishData = pubVersionService.ScanForEntriesAffectedByPubVer(id);

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

    boolean publishQualityIsOk = true;

    PublishDataWrapper publishData = pubVersionService.ScanForPrePublishedEntries();

    try {
      List<String> errors = publishData.getPublishErrors(getUserName());
      if (errors.size() > 0) {
        model.addAttribute("errors", errors);
        publishQualityIsOk = false;
        log.warn("Publish Preview quality check failed: \n" + String.join("\n", errors));
      }
      // Add processed entries to model and push to browser.
      populateModelWithPubVerSubEntries(model, publishData);

    } catch (Exception e) {
      publishQualityIsOk = false;
      log.error("Exception during Publish Preview: ", e);
    }

    // Mark in web model if publish data passed quality check.
    model.addAttribute("enablePublish", publishQualityIsOk);

    return "pubversion/create";
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

    try {
      Locktb lock = lockService.retrieveLock();
      PubVersion updatedPV = pubVersionService.add(instance, getUserName());
      lockService.releaseLock(lock);
      log.info("Publication successful. Pushing alert.");

      PublishDataWrapper publishData = pubVersionService.ScanForEntriesAffectedByPubVer(updatedPV.getId());
      alerterService.alertOnPublicering(updatedPV, publishData.getChangeReport());


      attributes.addFlashAttribute("message", "Publicering skapad.");
      return "redirect:/pubversion";

    } catch (Exception ex) {
      result.addError(new ObjectError("globalError", ex.toString()));
      return "pubversion/create";
    }
  }

  /**
   * Allows the logged-in user to download zipped backups of the PubVer snapshots.
   */
  @GetMapping("/pubversion/download/{PubVerId}")
  public void download(HttpServletResponse response, @PathVariable("PubVerId") Long pubVerId) {

    log.info("Request to download PV with id " + pubVerId + " received in controller.");

    try {
      PubVersion pubVersionInstance = pubVersionService.findById(pubVerId);
      if (pubVersionInstance != null) {
        log.info("PubVersion Instance located.");
        String filename = "PubVersion-" + pubVerId + ".json";
        streamFile(pubVersionInstance.getData(), filename, response);
      }
    }
    catch (IllegalArgumentException iae) {
      throw iae; //TODO: Error handling?
    }
  }

  /**
   * Performs the 'heavy lifting' of streaming a BLOB-file's content into a response stream.
   * This function will compress the File response as a gzip file.
   * @param file The file to be converted
   * @param fileName The name of the file to be held within the gzip container.
   * @param response The HttpServletResponse object that's objectifying a request's response.
   */
  private void streamFile(Blob file, String fileName, HttpServletResponse response) {
    log.info("Attempting to write File blob to response stream.");

    try {
      InputStream fileStream = file.getBinaryStream();

      response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".gzip");
      response.setHeader("Content-Length", String.valueOf(file.length()));
      response.setContentType("application/x-gzip");

      // Stream file content into response stream.
      ServletOutputStream output = response.getOutputStream();
      IOUtils.copy(fileStream, output);

      // Push and clean up.
      response.flushBuffer();
      output.close();
      fileStream.close();

      log.info("Write to response done.");

    } catch (SQLException | IOException e) {
      throw new RuntimeException(e);
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

  private String getUserName() {
    Subject subject = SecurityUtils.getSubject();
    return subject.getPrincipal().toString();
  }

  private void checkAdministratorRole() {
    if(!SecurityUtils.getSubject().hasRole("Administrator")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
  }
}
