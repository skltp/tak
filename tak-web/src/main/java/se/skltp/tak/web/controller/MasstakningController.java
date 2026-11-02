/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import se.skltp.tak.web.exception.MasstakningException;
import se.skltp.tak.web.service.MasstakningService;
import se.skltp.tak.web.util.PythonStyleJsonWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static se.skltp.tak.web.util.SecurityUtil.checkAdministratorRole;
import static se.skltp.tak.web.util.SecurityUtil.getUserName;

/**
 * Creates a TakOrder JSON file from a mass-TAKning Excel file, in the same way as the MkRoutingFromExcel.py script.
 */
@Controller
public class MasstakningController {

    private static final Logger log = LoggerFactory.getLogger(MasstakningController.class);

    private static final String VIEW = "masstakning/create";
    private static final String DEFAULT_PLATTFORM = "NTJP-PROD";
    private static final String DEFAULT_FILE_NAME = "takorder";

    private final MasstakningService masstakningService;

    public MasstakningController(MasstakningService masstakningService) {
        this.masstakningService = masstakningService;
    }

    @GetMapping("/masstakning")
    public String create(Model model) {
        checkAdministratorRole();
        return showForm(model, DEFAULT_PLATTFORM, MasstakningService.ACTION_TAKA, null);
    }

    @PostMapping("/masstakning")
    public String createTakOrder(Model model,
                                 @RequestParam(required = false) MultipartFile file,
                                 @RequestParam(defaultValue = DEFAULT_PLATTFORM) String plattform,
                                 @RequestParam(defaultValue = MasstakningService.ACTION_TAKA) String action,
                                 HttpServletResponse response) {
        checkAdministratorRole();
        try {
            if (file == null || file.isEmpty()) {
                throw new MasstakningException("Ingen excel-fil har angetts.");
            }
            Map<String, Object> takOrder = masstakningService.createTakOrder(file.getBytes(), plattform, action);
            byte[] json = PythonStyleJsonWriter.write(takOrder).getBytes(StandardCharsets.UTF_8);
            String fileName = takOrderFileName(file.getOriginalFilename());
            log.info("User {} created mass-TAKning TakOrder {} ({} {}) from {}",
                    getUserName(), fileName, action, plattform, file.getOriginalFilename());

            response.setContentType("application/json;charset=UTF-8");
            response.setContentLength(json.length);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(fileName));
            response.getOutputStream().write(json);
            // The response has been written, no view should be rendered
            return null;
        } catch (MasstakningException e) {
            return showForm(model, plattform, action, Collections.singletonList(e.getMessage()));
        } catch (IOException e) {
            log.error("Could not handle mass-TAKning file", e);
            return showForm(model, plattform, action,
                    Collections.singletonList("Kunde inte läsa excel-filen: " + e.getMessage()));
        }
    }

    /**
     * Same naming as the Python script: the Excel file name with the extension replaced by .json.
     */
    static String takOrderFileName(String excelFileName) {
        String name = excelFileName == null ? "" : excelFileName;
        // Some browsers send the full client path
        name = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1);
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            name = name.substring(0, dot);
        }
        return (name.isBlank() ? DEFAULT_FILE_NAME : name) + ".json";
    }

    static String contentDisposition(String fileName) {
        boolean ascii = StandardCharsets.US_ASCII.newEncoder().canEncode(fileName);
        ContentDisposition.Builder builder = ContentDisposition.attachment();
        return (ascii ? builder.filename(fileName) : builder.filename(fileName, StandardCharsets.UTF_8))
                .build().toString();
    }

    private String showForm(Model model, String plattform, String action, List<String> errors) {
        model.addAttribute("plattformar", MasstakningService.PLATTFORMAR);
        model.addAttribute("actions", List.of(MasstakningService.ACTION_TAKA, MasstakningService.ACTION_AVTAKA));
        model.addAttribute("plattform", plattform);
        model.addAttribute("action", action);
        if (errors != null) {
            model.addAttribute("errors", errors);
        }
        return VIEW;
    }
}
