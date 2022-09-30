package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigurationServiceTest {

    @Test
    public void testDefaultValuesWithoutFile() throws Exception {
        ConfigurationService service = new ConfigurationService();
        assertEquals("LOCAL", service.getEnvironment());
        assertEquals("inera-logo.png", service.getLogoImage());
        assertEquals("#ffffff;", service.getBackgroundStyle());
    }

    @Test
    public void testDefaultValuesWhenMissingInFile() throws Exception {
        ConfigurationService service = new ConfigurationService();
        File configFile = new File("src/test/resources/tak-web-config.properties");
        service.init(configFile);
        assertEquals("inera-logo.png", service.getLogoImage());
        assertEquals("#ffffff;", service.getBackgroundStyle());
    }

    @Test
    public void testFileValues() throws Exception {
        ConfigurationService service = new ConfigurationService();
        File configFile = new File("src/test/resources/tak-web-config.properties");
        service.init(configFile);
        assertEquals("TEST", service.getEnvironment());
    }
}
