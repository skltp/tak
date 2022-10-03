package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.ServletContext;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ConfigurationServiceTests {

    @Mock
    ServletContext context;

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
        service.init("src/test/resources/tak-web-config.properties", null);
        assertEquals("inera-logo.png", service.getLogoImage());
        assertEquals("#ffffff;", service.getBackgroundStyle());
    }

    @Test
    public void testFileValues() throws Exception {
        ConfigurationService service = new ConfigurationService();
        service.init("src/test/resources/tak-web-config.properties", null);
        assertEquals("TEST", service.getEnvironment());
    }

    @Test
    public void testCopyLogo(@TempDir Path tempDir) throws Exception {
        MockitoAnnotations.openMocks(this);
        when(context.getRealPath(any(String.class))).thenReturn(tempDir.toString());
        ConfigurationService service = new ConfigurationService();
        service.setServletContext(context);
        service.init("src/test/resources/tak-web-config-custom-logo.properties", "src/test/resources");
        Path expectedFile = tempDir.resolve("inera-logo-test.jpg");
        assertTrue(Files.exists(expectedFile));
    }
}
