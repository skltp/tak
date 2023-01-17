package se.skltp.tak.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.service.PublicationVersionService;

@Controller
public class PublicationVersionController {

  @Autowired
  PublicationVersionService pubVerService;

  @RequestMapping("/pubversion")
  public String index(Model model,
                      @RequestParam(defaultValue = "0")  Integer offset,
                      @RequestParam(defaultValue = "10") Integer max) {

    model.addAttribute("entityName", "PubVersion");

    PagedEntityList list = pubVerService.getEntityList(offset, max);

    model.addAttribute("list", list);
    model.addAttribute("basePath", "/pubversion");
    return "pubversion/list";
  }
}
