package se.skltp.tak.web.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.servlet.ServletContext;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/tak-web-config-custom-logo.properties")
public class ConfigurationServiceCustomLogoTests {

    @Mock ServletContext context;

    @Autowired ConfigurationService service;

    @Test
    public void testCopyLogo(@TempDir Path tempDir) throws Exception {
        MockitoAnnotations.openMocks(this);
        when(context.getRealPath(any(String.class))).thenReturn(tempDir.toString());
        service.setServletContext(context);
        service.init();
        Path expectedFile = tempDir.resolve("inera-logo-test.jpg");
        assertTrue(Files.exists(expectedFile));
    }
}
