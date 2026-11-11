/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.configuration.TestSecurityConfig;
import se.skltp.tak.web.service.ConfigurationService;
import se.skltp.tak.web.service.MasstakningService;
import se.skltp.tak.web.service.MasstakningServiceTest;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MasstakningController.class)
@Import({TestSecurityConfig.class, MasstakningService.class})
class MasstakningControllerTests {

    static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Autowired MockMvc mockMvc;

    @MockitoBean(name = "configurationService") ConfigurationService configurationService;

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void startPageShowsForm() throws Exception {
        mockMvc.perform(get("/masstakning"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Masstakning")))
                .andExpect(content().string(containsString("<option value=\"NTJP-PROD\" selected=\"selected\">")))
                .andExpect(content().string(containsString("<option value=\"SLL-QA\">")))
                .andExpect(content().string(containsString("<option value=\"TAKA\" selected=\"selected\">")))
                .andExpect(content().string(containsString("<option value=\"AV-TAKA\">")));
    }

    @Test
    void startPageRequiresLogin() throws Exception {
        mockMvc.perform(get("/masstakning"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void validFileIsDownloadedAsJson() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "Masstakning Region.xlsx", XLSX,
                MasstakningServiceTest.defaultWorkbook());

        mockMvc.perform(multipart("/masstakning").file(file).param("plattform", "NTJP-QA").param("action", "AV-TAKA"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", startsWith("application/json")))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"Masstakning Region.json\""))
                .andExpect(content().string(startsWith("{\n    \"plattform\": \"NTJP-QA\",\n")))
                .andExpect(content().string(containsString("\n    \"exkludera\": {\n")))
                .andExpect(content().string(containsString("Inera - Forvaltningsobjekt Nationella tjänsteplattformen")));
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void invalidFileShowsErrorAndKeepsSelections() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "fel.xlsx", XLSX, "not excel".getBytes());

        mockMvc.perform(multipart("/masstakning").file(file).param("plattform", "SLL-PROD").param("action", "AV-TAKA"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Ett fel upptäcktes i excel-filen")))
                .andExpect(content().string(containsString("<option value=\"SLL-PROD\" selected=\"selected\">")))
                .andExpect(content().string(containsString("<option value=\"AV-TAKA\" selected=\"selected\">")));
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void missingFileShowsError() throws Exception {
        mockMvc.perform(multipart("/masstakning").param("plattform", "NTJP-PROD").param("action", "TAKA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Ingen excel-fil har angetts.")));
    }

    @Test
    void takOrderFileNameReplacesExtension() {
        assertEquals("order.json", MasstakningController.takOrderFileName("order.xlsx"));
        assertEquals("order.v2.json", MasstakningController.takOrderFileName("order.v2.xlsx"));
        assertEquals("order.json", MasstakningController.takOrderFileName("C:\\Users\\x\\order.xlsx"));
        assertEquals("takorder.json", MasstakningController.takOrderFileName(null));
    }

    @Test
    void nonAsciiFileNameIsEncoded() {
        assertEquals("attachment; filename=\"=?UTF-8?Q?V=C3=A4st.json?=\"; filename*=UTF-8''V%C3%A4st.json",
                MasstakningController.contentDisposition("Väst.json"));
    }
}
