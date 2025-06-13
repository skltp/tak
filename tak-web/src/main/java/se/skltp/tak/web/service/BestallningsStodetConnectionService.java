package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.NoSuchSslBundleException;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.exception.CustomSSLConfigurationException;

import javax.net.ssl.*;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BestallningsStodetConnectionService {
    private static final Logger log = LoggerFactory.getLogger(BestallningsStodetConnectionService.class);

    ConfigurationService configurationService;

    @Autowired
    public BestallningsStodetConnectionService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public boolean isActive() {
        return configurationService.getBestallningOn();
    }

    public Set<String> checkBestallningConfiguration() {
        Set<String> configErrors = new HashSet<>();
        if (configurationService.getBestallningUrlsWithNames() == null) {
            configErrors.add("Det finns ingen url konfigurerad för beställningsstödet");
        }

        try {
            configurationService.getBestallningCertBundle();
            return configErrors;
        } catch (NoSuchSslBundleException ignored) {
        }

        if (configurationService.getBestallningClientCert() == null) {
            configErrors.add("Det finns inget certifikat konfigurerat för beställningsstödet");
        } else {
            File cert = configurationService.getBestallningClientCert().toFile();
            if (!cert.exists()) {
                configErrors.add("Konfigurerat certifikat för beställningsstödet hittades inte.");
            }
        }
        if (configurationService.getBestallningClientCertPassword() == null) {
            configErrors.add("Det finns inget lösenord konfigurerat till certifikatet för beställningsstödet.");
        }

        if (configurationService.getBestallningServerCert() == null) {
            configErrors.add("Det finns ingen truststore konfigurerad för beställningsstödet");
        } else {
            File cert = configurationService.getBestallningServerCert().toFile();
            if (!cert.exists()) {
                configErrors.add("Konfigurerad truststore för beställningsstödet hittades inte.");
            }
        }
        if (configurationService.getBestallningServerCertPassword() == null) {
            configErrors.add("Det finns inget lösenord konfigurerat till truststore för beställningsstödet.");
        }

        return configErrors;
    }

    public List<Map<String, String>> getBestallningUrlsWithNames() {
        return configurationService.getBestallningUrlsWithNames();
    }

    public String getBestallningByUrl(long bestallningsNummer, String url) throws Exception {
        log.info("Hämtar beställning nummer {} från URL {}", bestallningsNummer, url);

        URL tempUrl = new URL(url);

        if (tempUrl.getHost() == null || tempUrl.getHost().isEmpty()) {
            throw new IllegalArgumentException("Invalid URL: Missing or empty host in " + url);
        }

        if (tempUrl.getPath() == null) {
            throw new IllegalArgumentException("Invalid URL: Path is null for " + url);
        }
        URL fullUrl = new URL(url + bestallningsNummer);

        HttpURLConnection con;

        try {
            if (fullUrl.getProtocol().equalsIgnoreCase("https")) {
                con = (HttpsURLConnection) fullUrl.openConnection();
                ((HttpsURLConnection) con).setSSLSocketFactory(prepareSSLContext().getSocketFactory());
            } else if (fullUrl.getProtocol().equalsIgnoreCase("http")) {
                con = (HttpURLConnection) fullUrl.openConnection();
            } else {
                throw new IllegalArgumentException("Unsupported protocol: " + fullUrl.getProtocol());
            }

            try (InputStream stream = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                StringBuilder jsonBestallning = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null) {
                    jsonBestallning.append(str).append("\n");
                }
                return jsonBestallning.toString();
            }
        } catch (FileNotFoundException e) {
            log.error("Resource not found at URL: {}", fullUrl, e);
            throw new RuntimeException("Resource not found.", e);
        } catch (IllegalStateException ex) {
            String causeMessage = (ex.getMessage() != null) ? ex.getMessage() : "Okänt fel vid SSL anslutning";
            String errorMessage = String.format("SSL fel vid anslutning till %s: %s", fullUrl, causeMessage);
            throw new CustomSSLConfigurationException(errorMessage, ex);
        } catch (ConnectException e) {
            log.error("Connection refused when connecting to {}", fullUrl, e);
            throw new RuntimeException(String.format("Kunde inte ansluta till server på URL: %s. Försök senare.", fullUrl), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SSLContext prepareSSLContext() throws Exception {
        try {
            SslBundle sslBundle = configurationService.getBestallningCertBundle();
            return sslBundle.createSslContext();
        } catch (NoSuchSslBundleException e) {
            log.info("Could not find a configured bundle falling back on legacy configuration");
        }
        File cert = configurationService.getBestallningClientCert().toFile();
        String pw = configurationService.getBestallningClientCertPassword();
        String type = configurationService.getBestallningClientCertType();
        KeyManager[] keyManagers = getKeyManagers(type, new FileInputStream(cert), pw);

        File serverCert = configurationService.getBestallningServerCert().toFile();
        String serverType = configurationService.getBestallningServerCertType();
        String serverPw = configurationService.getBestallningServerCertPassword();
        TrustManager[] trustManagers = getTrustManagers(serverType, new FileInputStream(serverCert), serverPw);

        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(keyManagers, trustManagers, new SecureRandom());
        return ctx;
    }

    private static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword)
            throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }

    private static TrustManager[] getTrustManagers(String trustStoreType, InputStream trustStoreFile, String trustStorePassword)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType);
        trustStore.load(trustStoreFile, trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }
}
