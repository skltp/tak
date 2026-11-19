/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@TestPropertySource("/tak-web-config-custom-logo.properties")
public class ConfigurationServiceCustomLogoTests {

    @Autowired ConfigurationService service;

    @Test
    public void initDoesNotThrowWithCustomLogo() {
        // With the old implementation, init() would throw NoSuchFileException
        // when running as a Spring Boot Fat JAR because the Tomcat docbase
        // subdirectory /static/images does not exist on disk.
        // The new implementation delegates image serving to Spring MVC
        // ResourceHandler and does not attempt to copy files at runtime.
        assertDoesNotThrow(() -> service.init());
    }

    @Test
    public void customLogoPropertyIsReadCorrectly() {
        assertEquals("inera-logo-test.jpg", service.getLogoImage());
    }
}
