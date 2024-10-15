package se.skltp.tak.web.controller;

import static se.skltp.tak.web.util.SecurityUtil.getUserName;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.entity.Locktb;
import se.skltp.tak.web.service.AlerterService;
import se.skltp.tak.web.service.LockService;
import se.skltp.tak.web.service.PubVersionService;
import se.skltp.tak.web.util.PublishDataWrapper;

@Controller
public class PubVersionController {

  @Autowired PubVersionService pubVersionService;
  @Autowired LockService lockService;
  @Autowired AlerterService alerterService;

  private static final Logger log = LoggerFactory.getLogger(PubVersionController.class);
  private static final String MESSAGE_FLASH_ATTRIBUTE = "message";
  private static final String ERRORS_FLASH_ATTRIBUTE = "errors";

  @RequestMapping(value = "/pubversion", method = {RequestMethod.POST, RequestMethod.GET})
  public String index(Model model,
                      @RequestParam(defaultValue = "0")  Integer offset,
                      @RequestParam(defaultValue = "10") Integer max,
                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                      @RequestParam(value = "utforare", required = false) String utforare
  ) {
    modelBasicPrep(model);

    List<String> utforareList = pubVersionService.findAllUniqueUtforare();
    model.addAttribute("utforareList", utforareList);

    List<ListFilter> filters = getListFilters(startDate, endDate, utforare);

    PagedEntityList<PubVersion> list = pubVersionService.getEntityList(offset, max, filters, "id", true);

    model.addAttribute("list", list);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("utforare", utforare);

    return "pubversion/list";
  }

  private List<ListFilter> getListFilters(LocalDate startDate, LocalDate endDate, String utforare) {
    List<ListFilter> filters = new ArrayList<>();

    if(utforare!= null && !utforare.isEmpty()  && !"all".equals(utforare)) {
      filters.add(new ListFilter("utforare", FilterCondition.EQUALS, utforare));
    }
    if(startDate != null) {
      filters.add(new ListFilter("time", FilterCondition.FROM, Timestamp.valueOf(startDate.atStartOfDay())));
    }
    if(endDate != null) {
      filters.add(new ListFilter("time", FilterCondition.TO, Timestamp.valueOf(endDate.atStartOfDay())));
    }
    return filters;
  }

  @RequestMapping(value = "/pubversion/{id}", method = {RequestMethod.POST, RequestMethod.GET})
  public String show(Model model,
                     @PathVariable Long id) {
    modelBasicPrep(model);

    PubVersion instance = pubVersionService.findById(id);
    model.addAttribute("instance", instance);

    PublishDataWrapper publishData = pubVersionService.scanForEntriesAffectedByPubVer(id);

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
    model.addAttribute(MESSAGE_FLASH_ATTRIBUTE, alerterService.getMailAlertStatusMessage());

    // Add blank pubVer to model.
    model.addAttribute("instance", new PubVersion());

    boolean publishQualityIsOk = true;

    PublishDataWrapper publishData = pubVersionService.scanForPrePublishedEntries();

    try {
      List<String> errors = publishData.getPublishErrors(getUserName());
      if (!errors.isEmpty()) {
        model.addAttribute(ERRORS_FLASH_ATTRIBUTE, errors);
        publishQualityIsOk = false;
        log.warn("Publish Preview quality check failed: \n" + String.join("\n", errors));
      }
      // Add processed entries to model and push to browser.
      populateModelWithPubVerSubEntries(model, publishData);

    } catch (Exception e) {
      publishQualityIsOk = false;
      model.addAttribute(ERRORS_FLASH_ATTRIBUTE, Arrays.asList("Fel vid Granska publicering: " + e));
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

    try {
      Locktb lock = lockService.retrieveLock();
      PubVersion updatedPV = pubVersionService.add(instance, getUserName());
      lockService.releaseLock(lock);
      log.info("Publication successful. Pushing alert.");

      PublishDataWrapper publishData = pubVersionService.scanForEntriesAffectedByPubVer(updatedPV.getId());
      alerterService.alertOnPublicering(updatedPV, publishData.getChangeReport());

      attributes.addFlashAttribute(MESSAGE_FLASH_ATTRIBUTE, String.format("Publicerad version %s skapad.", updatedPV.getId()));
      return "redirect:/pubversion/" + updatedPV.getId();
    }
    catch (Exception ex) {
      result.addError(new ObjectError("globalError", ex.toString()));
      return "pubversion/create";
    }
  }

  @RequestMapping("/pubversion/rollback/{id}")
  public String rollback(@PathVariable Long id, RedirectAttributes attributes) {
    try {
      PubVersion pv = pubVersionService.findById(id);

      Locktb lock = lockService.retrieveLock();
      pubVersionService.rollback(id, getUserName());
      lockService.releaseLock(lock);

      alerterService.alertOnRollback(pv);
      attributes.addFlashAttribute(MESSAGE_FLASH_ATTRIBUTE, String.format("Rollback av Publicerad version %s genomförd.", id));
    }
    catch (Exception e) {
      log.error("Rollback failed: ", e);
    }

    return "redirect:/pubversion";
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
    catch (Exception e) {
      log.error("Failed to serve file: ", e);
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

}
