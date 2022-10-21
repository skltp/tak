package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

@Service
public class BestallningsStodetConnectionService {

    @Autowired
    ConfigurationService configurationService;

    public String getBestallning(long bestallningsNummer) throws Exception {
        StringBuilder jsonBestallning = new StringBuilder();

        URL url = new URL(configurationService.getBestallningUrl() + bestallningsNummer);

        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setSSLSocketFactory(prepareSSLContext().getSocketFactory());

        InputStream stream = (InputStream) con.getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String str;
        while ((str = br.readLine()) != null) {
            jsonBestallning.append(str).append("\n");
        }
        br.close();

        return jsonBestallning.toString();
    }

    private SSLContext prepareSSLContext() throws Exception {
        File cert = configurationService.getBestallningClientCert().toFile();
        String pw = configurationService.getBestallningClientCertPassword();
        KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(cert), pw);

        File serverCert = configurationService.getBestallningServerCert().toFile();
        String serverPw = configurationService.getBestallningServerCertPassword();
        TrustManager[] trustManagers = getTrustManagers("jks", new FileInputStream(serverCert), serverPw);

        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(keyManagers, trustManagers, new SecureRandom());
        return ctx;
    }

    private static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }

    private static TrustManager[] getTrustManagers(String trustStoreType, InputStream trustStoreFile, String trustStorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType);
        trustStore.load(trustStoreFile, trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }
}
