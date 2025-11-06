package se.skltp.tak.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.dto.connection.ConnectionStatusExport;
import se.skltp.tak.web.mapper.connection.ConnectionStatusExportMapper;
import se.skltp.tak.web.service.ConnectionsService;

import java.util.List;

import static se.skltp.tak.web.util.SecurityUtil.checkAdministratorRole;

@Controller
public class ConnectionsController {

    private static final Logger log = LoggerFactory.getLogger(ConnectionsController.class);
    public static final String EXPORT_FILENAME = "anslutningar.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConnectionStatusExportMapper connectionStatusExportMapper;

    private final ConnectionsService connectionsService;

    public ConnectionsController(ConnectionStatusExportMapper connectionStatusExportMapper, ConnectionsService connectionsService) {
        this.connectionStatusExportMapper = connectionStatusExportMapper;
        this.connectionsService = connectionsService;
        objectMapper.registerModule(new Jdk8Module());
    }

    @GetMapping("/connections")
    public String connections(Model model, @RequestParam(defaultValue = "0") Integer offset, @RequestParam(defaultValue = "10") Integer max) {
        log.debug("GET /connections {} {} {}", model, offset, max);
        checkAdministratorRole();
        model.addAttribute("entityName", connectionsService.getEntityName());
        PagedEntityList<ConnectionStatus> list = connectionsService.getActive(offset, max);
        log.debug("list {}", list);
        model.addAttribute("list", list);
        model.addAttribute("basePath", "/connections");
        return "connections/list";
    }

    @GetMapping("/connections/export")
    public ResponseEntity<String> export(Model model) throws JsonProcessingException {
        log.debug("GET /connections/export {}", model);
        checkAdministratorRole();
        PagedEntityList<ConnectionStatus> list = connectionsService.getActive(0, Integer.MAX_VALUE);
        log.debug("list {}", list);
        List<ConnectionStatusExport> exports = connectionStatusExportMapper.toExportDto(list.getContent());
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exports);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", EXPORT_FILENAME))
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}
