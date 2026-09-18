/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests that custom logo images placed in the external resource directory
 * (tak.web.resource.dir) are served correctly via HTTP without any runtime
 * file copying.
 *
 * <p>Background: Prior to this fix, ConfigurationService.init() attempted to
 * copy custom logo files from the resource directory to a temporary Tomcat
 * docbase path using {@code context.getRealPath("/static/images")}. In Spring
 * Boot Fat JAR deployments the target subdirectory does not exist on disk,
 * causing a {@code NoSuchFileException} at startup.</p>
 *
 * <p>The fix registers the external resource directory as a Spring MVC
 * resource location in {@code WebConfig}, allowing Spring to serve images
 * directly without file copying.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CustomLogoResourceHandlerTests {

    // Create a temp directory that acts as the external resource dir (/etc/tak)
    @TempDir
    static Path externalResourceDir;

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("tak.web.resource.dir", () -> externalResourceDir.toString());
        registry.add("tak.image.logo", () -> "custom-logo.png");
    }

    @Test
    void customLogoFromExternalDirectoryIsServedViaHttp() throws Exception {
        // Place a custom logo file in the external resource directory
        Path logoFile = externalResourceDir.resolve("custom-logo.png");
        Files.write(logoFile, new byte[]{(byte) 0x89, 'P', 'N', 'G'}); // Minimal PNG-like bytes

        // Verify that Spring MVC serves the custom logo directly from the external directory
        mockMvc.perform(get("/static/images/custom-logo.png"))
                .andExpect(status().isOk());
    }

    @Test
    void defaultLogoFromClasspathIsStillServed() throws Exception {
        // The default logo (inera-logo.png) is embedded in classpath:/static/images/
        // It should still be accessible even when an external resource dir is configured
        mockMvc.perform(get("/static/images/inera-logo.png"))
                .andExpect(status().isOk());
    }

    @Test
    void customLogoFromClasspathIsServedWhenNotInExternalDirectory() throws Exception {
        // inera-logo-test.jpg exists in classpath:/static/images/ (e.g. baked into the JAR),
        // but is NOT present in the external directory. It should be served directly from classpath.
        mockMvc.perform(get("/static/images/inera-logo-test.jpg"))
                .andExpect(status().isOk());
    }

    @Test
    void externalFileOverridesClasspathFileWhenBothExist() throws Exception {
        // When a file exists in both the external directory and classpath,
        // the external file must take priority (override behavior).
        Path overrideFile = externalResourceDir.resolve("inera-logo-test.jpg");
        byte[] externalBytes = "EXTERNAL_OVERRIDDEN_LOGO_BYTES".getBytes();
        try {
            Files.write(overrideFile, externalBytes);
            mockMvc.perform(get("/static/images/inera-logo-test.jpg"))
                    .andExpect(status().isOk())
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().bytes(externalBytes));
        } finally {
            Files.deleteIfExists(overrideFile);
        }
    }

    @Test
    void nonExistentLogoReturns404() throws Exception {
        // A logo that does not exist in either location should return 404
        mockMvc.perform(get("/static/images/does-not-exist.png"))
                .andExpect(status().isNotFound());
    }
}

