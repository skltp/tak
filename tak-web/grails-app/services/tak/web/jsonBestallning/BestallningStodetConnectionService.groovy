/**
 * Copyright (c) 2013 Center för eHälsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package tak.web.jsonBestallning

import com.fasterxml.jackson.databind.ObjectMapper
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import tak.web.I18nService

import javax.net.ssl.*
import java.security.KeyStore
import java.security.SecureRandom

class BestallningStodetConnectionService {

    def grailsApplication
    I18nService i18nService


    Boolean isJsonBestallningOn() {
        return grailsApplication.config.tak.bestallning.on.size() != 0 && Boolean.parseBoolean(grailsApplication.config.tak.bestallning.on);
    }

    Set<String> validateConnectionConfig() {
        Set<String> configErrors = new HashSet<>()
        if (grailsApplication.config.tak.bestallning.url?.isEmpty()) {
            configErrors.add(i18nService.msg("bestallning.error.url"))
        }
        if (grailsApplication.config.tak.bestallning.cert?.isEmpty()) {
            configErrors.add(i18nService.msg("bestallning.error.cert"))
        } else {
            String cert = grailsApplication.config.tak.bestallning.cert
            File f = new File(System.getenv("TAK_HOME") + "/security/" + cert)
            if (!f.exists()) {
                configErrors.add(i18nService.msg("bestallning.error.certNotFound"))
            }
        }
        if (grailsApplication.config.tak.bestallning.pw?.isEmpty()) {
            configErrors.add(i18nService.msg("bestallning.error.pw"))
        }
        if (grailsApplication.config.tak.bestallning.serverCert?.isEmpty()) {
            configErrors.add(i18nService.msg("bestallning.error.servercert"))
        } else {
            String serverCert = grailsApplication.config.tak.bestallning.serverCert
            File f = new File(System.getenv("TAK_HOME") + "/security/" + serverCert)
            if (!f.exists()) {
                configErrors.add(i18nService.msg("bestallning.error.serverCertNotFound"))
            }
        }
        if (grailsApplication.config.tak.bestallning.serverPw?.isEmpty()) {
            configErrors.add(i18nService.msg("bestallning.error.serverpw"))
        }
        return configErrors
    }


    String getJsonBestallningFromBS(long num) {
        String jsonBestallning = "";

        prepareSSLContext();

        String urlString = grailsApplication.config.tak.bestallning.url
        URL url = new URL(urlString + num)

        log.info("Download jsonBestallning from url: " + url)

        HttpsURLConnection con = (HttpsURLConnection) url.openConnection()

        InputStream stream = (InputStream) con.getContent()
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))
        String str
        while ((str = br.readLine()) != null) {
            jsonBestallning += str + "\n"
        }
        br.close()
        return jsonBestallning
    }


    boolean simpleValidate(String jsonBestallning) {
        return jsonBestallning != null && jsonBestallning.contains("{") && jsonBestallning.contains("}")
    }

    String validateAndFormat(String jsonBestallning) {
        jsonBestallning = jsonBestallning.substring(jsonBestallning.indexOf("{"), jsonBestallning.lastIndexOf("}") + 1)
        ObjectMapper mapper = new ObjectMapper()
        JsonBestallning json = mapper.readValue(jsonBestallning, JsonBestallning.class)
        String formattedBestallning = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)
        log.info("JsonBestallning: \n " + formattedBestallning)
        return formattedBestallning
    }

    private void prepareSSLContext() {
        String pw = grailsApplication.config.tak.bestallning.pw
        String cert = grailsApplication.config.tak.bestallning.cert
        String serverCert = grailsApplication.config.tak.bestallning.serverCert
        String serverPw = grailsApplication.config.tak.bestallning.serverPw

        File f = new File(System.getenv("TAK_HOME") + "/security/" + cert)
        File f2 = new File(System.getenv("TAK_HOME") + "/security/" + serverCert)

        SSLContext ctx = SSLContext.getInstance("TLS")
        KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(f), pw)
        TrustManager[] trustManagers = getTrustManagers("jks", new FileInputStream(f2), serverPw)
        ctx.init(keyManagers, trustManagers, new SecureRandom())
        SSLContext.setDefault(ctx)
    }

    private
    static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray())
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(keyStore, keyStorePassword.toCharArray())
        return kmf.getKeyManagers()
    }

    private
    static TrustManager[] getTrustManagers(String trustStoreType, InputStream trustStoreFile, String trustStorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType)
        trustStore.load(trustStoreFile, trustStorePassword.toCharArray())
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(trustStore)
        return tmf.getTrustManagers()
    }
}
