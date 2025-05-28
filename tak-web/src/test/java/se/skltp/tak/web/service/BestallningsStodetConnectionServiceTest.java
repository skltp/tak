package se.skltp.tak.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.boot.ssl.SslBundle;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BestallningsStodetConnectionServiceTest {
    private BestallningsStodetConnectionService bestallningsStodetConnectionService;

    @Mock
    private ConfigurationService configurationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bestallningsStodetConnectionService = new BestallningsStodetConnectionService(configurationService);
    }

    @Test
    void testIsActive_WhenConfigurationServiceReturnsTrue() {
        when(configurationService.getBestallningOn()).thenReturn(true);

        boolean result = bestallningsStodetConnectionService.isActive();

        assertTrue(result);
    }

    @Test
    void testCheckBestallningConfiguration_MissingUrlsWithNames() {
        when(configurationService.getBestallningUrlsWithNames()).thenReturn(null);

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(1, result.size());
        assertEquals(Set.of("Det finns ingen url konfigurerad för beställningsstödet"), result);
    }

    @Test
    void testCheckBestallningConfiguration_NoErrorsWhenCertBundleExists(){
        when(configurationService.getBestallningUrls()).thenReturn(List.of("http://thematrix.com"));
        when(configurationService.getBestallningCertBundle()).thenReturn(mock(SslBundle.class));

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(0, result.size());
    }

    @Test
    void testCheckBestallningConfiguration_MissingClientCert() {
        List<String> bsUrls = List.of("http://localhost:8080", "https://localhost:8081");
        when(configurationService.getBestallningUrls()).thenReturn(bsUrls);
        //when(configurationService.getBestallningUrls()).thenReturn(List.of("http://thematrix.com"));
        when(configurationService.getBestallningCertBundle()).thenThrow(new NoSuchSslBundleException("Test Bundle", "Test Exception"));
        when(configurationService.getBestallningClientCertPassword()).thenReturn("password");
        when(configurationService.getBestallningClientCertPassword()).thenReturn("password");
        Path mockTruststorePath = mock(Path.class);
        File mockTruststoreFile = mock(File.class);
        when(mockTruststorePath.toFile()).thenReturn(mockTruststoreFile);
        when(mockTruststoreFile.exists()).thenReturn(true);
        when(configurationService.getBestallningServerCert()).thenReturn(mockTruststorePath);
        when(configurationService.getBestallningServerCertPassword()).thenReturn("truststorePassword");

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(1, result.size());
        assertEquals(Set.of("Det finns inget certifikat konfigurerat för beställningsstödet"), result);
    }

    @Test
    void testCheckBestallningConfiguration_ClientCertExists() {
        when(configurationService.getBestallningUrls()).thenReturn(List.of("http://thematrix.com"));
        when(configurationService.getBestallningCertBundle()).thenThrow(new NoSuchSslBundleException("Test Bundle", "Test Exception"));

        Path mockClientCertPath = mock(Path.class);
        File mockClientCertFile = mock(File.class);

        when(mockClientCertPath.toFile()).thenReturn(mockClientCertFile);
        when(mockClientCertFile.exists()).thenReturn(true);

        when(configurationService.getBestallningClientCert()).thenReturn(mockClientCertPath);
        when(configurationService.getBestallningClientCertPassword()).thenReturn("password");


        Path mockTruststorePath = mock(Path.class);
        File mockTruststoreFile = mock(File.class);
        when(mockTruststorePath.toFile()).thenReturn(mockTruststoreFile);
        when(mockTruststoreFile.exists()).thenReturn(true);
        when(configurationService.getBestallningServerCert()).thenReturn(mockTruststorePath);
        when(configurationService.getBestallningServerCertPassword()).thenReturn("truststorePassword");

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertTrue(result.isEmpty()); // No errors should be added because the cert exists
    }

    @Test
    void testCheckBestallningConfiguration_ClientCertDoesNotExist() {
        when(configurationService.getBestallningUrls()).thenReturn(List.of("http://thematrix.com"));
        when(configurationService.getBestallningCertBundle()).thenThrow(new NoSuchSslBundleException("Test Bundle", "Test Exception"));

        Path mockClientCertPath = mock(Path.class);
        File mockClientCertFile = mock(File.class);

        when(mockClientCertPath.toFile()).thenReturn(mockClientCertFile);
        when(mockClientCertFile.exists()).thenReturn(false);

        when(configurationService.getBestallningClientCert()).thenReturn(mockClientCertPath);
        when(configurationService.getBestallningClientCertPassword()).thenReturn("password");

        Path mockTruststorePath = mock(Path.class);
        File mockTruststoreFile = mock(File.class);
        when(mockTruststorePath.toFile()).thenReturn(mockTruststoreFile);
        when(mockTruststoreFile.exists()).thenReturn(true);
        when(configurationService.getBestallningServerCert()).thenReturn(mockTruststorePath);
        when(configurationService.getBestallningServerCertPassword()).thenReturn("truststorePassword");

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(1, result.size());
        assertEquals(Set.of("Konfigurerat certifikat för beställningsstödet hittades inte."), result);
    }

    @Test
    void testCheckBestallningConfiguration_MissingClientCertPassword() {
        when(configurationService.getBestallningUrls()).thenReturn(List.of("http://thematrix.com"));
        when(configurationService.getBestallningCertBundle()).thenThrow(new NoSuchSslBundleException("Test Bundle", "Test Exception"));
        Path mockClientCertPath = mock(Path.class); // Mock the Path object
        File mockClientCertFile = mock(File.class); // Mock the File object the Path will return
        when(mockClientCertPath.toFile()).thenReturn(mockClientCertFile); // Path.toFile() returns mock File
        when(mockClientCertFile.exists()).thenReturn(true); // Simulate file exists
        when(configurationService.getBestallningClientCert()).thenReturn(mockClientCertPath); // Return mock Path
        when(configurationService.getBestallningClientCertPassword()).thenReturn(null);
        Path mockTruststorePath = mock(Path.class);
        File mockTruststoreFile = mock(File.class);
        when(mockTruststorePath.toFile()).thenReturn(mockTruststoreFile);
        when(mockTruststoreFile.exists()).thenReturn(true); // Ensure truststore file exists
        when(configurationService.getBestallningServerCert()).thenReturn(mockTruststorePath);
        when(configurationService.getBestallningServerCertPassword()).thenReturn("truststorePassword");

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(1, result.size());
        assertEquals(Set.of("Det finns inget lösenord konfigurerat till certifikatet för beställningsstödet."), result);
    }

    @Test
    void testCheckBestallningConfiguration_ClientCertPasswordPresent() {
        when(configurationService.getBestallningClientCertPassword()).thenReturn("securePassword"); // Simulate a valid password

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertTrue(result.isEmpty());
    }


    @Test
    void testCheckBestallningConfiguration_ServerCertIsNull() {
        when(configurationService.getBestallningUrls()).thenReturn(List.of("http://thematrix.com"));
        when(configurationService.getBestallningCertBundle()).thenThrow(new NoSuchSslBundleException("Test Bundle", "Test Exception"));
        when(configurationService.getBestallningClientCertPassword()).thenReturn("password");
        when(configurationService.getBestallningServerCertPassword()).thenReturn("truststorePassword");
        Path mockClientCertPath = mock(Path.class);
        File mockClientCertFile = mock(File.class);
        when(mockClientCertPath.toFile()).thenReturn(mockClientCertFile);
        when(mockClientCertFile.exists()).thenReturn(true);
        when(configurationService.getBestallningClientCert()).thenReturn(mockClientCertPath);

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(1, result.size());
        assertEquals(Set.of("Det finns ingen truststore konfigurerad för beställningsstödet"), result);
    }

    @Test
    void testCheckBestallningConfiguration_ServerCertFileDoesNotExist() {
        when(configurationService.getBestallningUrls()).thenReturn(List.of("http://thematrix.com"));
        when(configurationService.getBestallningCertBundle()).thenThrow(new NoSuchSslBundleException("Test Bundle", "Test Exception"));
        when(configurationService.getBestallningClientCertPassword()).thenReturn("password");
        when(configurationService.getBestallningServerCertPassword()).thenReturn("truststorePassword");
        Path mockClientCertPath = mock(Path.class);
        File mockClientCertFile = mock(File.class);
        when(mockClientCertPath.toFile()).thenReturn(mockClientCertFile);
        when(mockClientCertFile.exists()).thenReturn(true);
        when(configurationService.getBestallningClientCert()).thenReturn(mockClientCertPath);
        Path mockPath = mock(Path.class);
        File mockFile = mock(File.class);
        when(configurationService.getBestallningServerCert()).thenReturn(mockPath);
        when(mockPath.toFile()).thenReturn(mockFile);
        when(mockFile.exists()).thenReturn(false);

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(1, result.size());
        assertEquals(Set.of("Konfigurerad truststore för beställningsstödet hittades inte."), result);
    }

    @Test
    void testCheckBestallningConfiguration_ServerCertFileExists() {
        Path mockPath = mock(Path.class);
        File mockFile = mock(File.class);
        when(configurationService.getBestallningServerCert()).thenReturn(mockPath);
        when(mockPath.toFile()).thenReturn(mockFile);
        when(mockFile.exists()).thenReturn(true);

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(0, result.size()); // No errors should be added
    }

    @Test
    void testCheckBestallningConfiguration_ServerCertPasswordIsNull() {
        when(configurationService.getBestallningUrls()).thenReturn(List.of("http://thematrix.com"));
        when(configurationService.getBestallningCertBundle()).thenThrow(new NoSuchSslBundleException("Test Bundle", "Test Exception"));
        when(configurationService.getBestallningClientCertPassword()).thenReturn("password");
        when(configurationService.getBestallningServerCertPassword()).thenReturn("truststorePassword");
        Path mockClientCertPath = mock(Path.class);
        File mockClientCertFile = mock(File.class);
        when(mockClientCertPath.toFile()).thenReturn(mockClientCertFile);
        when(mockClientCertFile.exists()).thenReturn(true);
        when(configurationService.getBestallningClientCert()).thenReturn(mockClientCertPath);
        Path mockPath = mock(Path.class);
        File mockFile = mock(File.class);
        when(configurationService.getBestallningServerCert()).thenReturn(mockPath);
        when(mockPath.toFile()).thenReturn(mockFile);
        when(mockFile.exists()).thenReturn(true);
        when(configurationService.getBestallningServerCertPassword()).thenReturn(null);

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(1, result.size());
        assertEquals(Set.of("Det finns inget lösenord konfigurerat till truststore för beställningsstödet."), result);
    }

    @Test
    void testCheckBestallningConfiguration_ServerCertPasswordIsPresent() {
        when(configurationService.getBestallningServerCertPassword()).thenReturn("securePassword");

        Set<String> result = bestallningsStodetConnectionService.checkBestallningConfiguration();

        assertEquals(0, result.size()); // No errors should be added
    }

    @Test
    void testGetBestallning_InvalidProtocol_ShouldThrowException() {
        String invalidUrl = "ftp://example.com/bestallning";
        long bestallningsNummer = 12345L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bestallningsStodetConnectionService.getBestallningByUrl(bestallningsNummer, invalidUrl);
        });

        assertEquals("Unsupported protocol: ftp", exception.getMessage());
    }

    @Test
    void testGetBestallningByUrl_EmptyUrl_ShouldThrowException() {
        MalformedURLException exception = assertThrows(MalformedURLException.class, () -> {
            bestallningsStodetConnectionService.getBestallningByUrl(12345L, "");
        });

        assertEquals("no protocol: ", exception.getMessage());
    }

    @Test
    void testGetBestallningByUrl_MalformedUrl_ShouldThrowException() {
        String malformedUrl = "http://";
        long bestallningsNummer = 12345L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bestallningsStodetConnectionService.getBestallningByUrl(bestallningsNummer, malformedUrl);
        });

        assertTrue(exception.getMessage().contains("Invalid URL"));
    }

}