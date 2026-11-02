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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import se.skltp.tak.web.configuration.TestSecurityConfig;
import se.skltp.tak.web.service.ConfigurationService;
import se.skltp.tak.web.service.MasstakningService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MasstakningController.class)
@Import({TestSecurityConfig.class})
class MasstakningControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MasstakningService masstakningService;
    @MockitoBean(name = "configurationService") private ConfigurationService configurationService;

    @Test
    @WithMockUser(username = TestSecurityConfig.TEST_USER)
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/masstakning").secure(true))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = TestSecurityConfig.ADMIN_USER, roles = {"ADMIN"})
    void testAuthorized() throws Exception {
        mockMvc.perform(get("/masstakning").secure(true))
                .andExpect(status().isOk());
    }
}
