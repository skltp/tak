package se.skltp.tak.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.service.ConnectionsService;

import static se.skltp.tak.web.util.SecurityUtil.checkAdministratorRole;

@Controller
public class ConnectionsController {

    private static final Logger log = LoggerFactory.getLogger(ConnectionsController.class);

    private final ConnectionsService connectionsService;

    public ConnectionsController(ConnectionsService connectionsService) {
        this.connectionsService = connectionsService;
    }

    @GetMapping("/connections")
    public String index(Model model, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "10") Integer max) {
        log.debug("index {} {} {}", model, offset, max);
        checkAdministratorRole();
        model.addAttribute("entityName", "Anslutningar");
        PagedEntityList<ConnectionStatus> list = connectionsService.getActive(offset, max);
        log.debug("list {}", list);
        model.addAttribute("list", list);
        model.addAttribute("basePath", "/connections");
        return "connections/list";
    }
}
