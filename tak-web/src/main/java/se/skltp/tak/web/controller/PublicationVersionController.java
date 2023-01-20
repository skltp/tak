package se.skltp.tak.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.PublicationVersionService;
import se.skltp.tak.web.service.RivTaProfilService;

import java.util.Optional;

@Controller
public class PublicationVersionController {

  @Autowired
  PublicationVersionService pubVerService;

  // List data services.
  @Autowired
  RivTaProfilService rivTaProfilService;


  @RequestMapping("/pubversion")
  public String index(Model model,
                      @RequestParam(defaultValue = "0")  Integer offset,
                      @RequestParam(defaultValue = "10") Integer max) {
    modelPrep(model);

    PagedEntityList list = pubVerService.getEntityList(offset, max);
    model.addAttribute("list", list);

    return "pubversion/list";
  }

  @RequestMapping("/pubversion/{id}")
  public String show(Model model,
                     @PathVariable Long id) {
    //checkAdministratorRole();
    modelPrep(model);

    Optional<PubVersion> instance = pubVerService.findById(id);
    if (!instance.isPresent()) throw new IllegalArgumentException("Entity not found");
    model.addAttribute("instance", instance.get());

    model.addAttribute("rivTaProfilList", rivTaProfilService.findAllByPubVersion(id));

    // Todo: Populate model with content-lists.
    return "pubversion/show";
  }

  private static void modelPrep(Model model) {
    model.addAttribute("entityName", "PubVersion");
    model.addAttribute("basePath", "/pubversion");
  }
}
