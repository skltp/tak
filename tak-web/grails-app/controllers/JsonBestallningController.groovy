import org.apache.commons.logging.LogFactory
import org.springframework.web.context.request.RequestContextHolder
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import tak.web.BestallningService
import tak.web.I18nService

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import java.security.KeyStore
import java.security.SecureRandom


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


class JsonBestallningController {
    private static final log = LogFactory.getLog(this)

    I18nService i18nService;
    BestallningService bestallningService;

    def create() {
        //Before rendering view create, check if user has configured use of cert to get 'bestallning' from provider by number.
        boolean set = false
        if (bestallningService.getBestallningCert() != null && !bestallningService.getBestallningCert().isEmpty()) {
            set = true
        }
        render(view: 'create', model: [hasCertConfigured: set])
    }

    /**
     * If user has configured a valid certificate (see tak-web-config.properties), then it should be possible to get the json-file directly from
     * the provider, and to display the content in the web page, for validation.
     */
    def loadcreate() {
        String urlString
        urlString = bestallningService.bestallningUrl
        String pw = bestallningService.getBestallningPw()
        String cert = bestallningService.getBestallningCert()

        def bestNum = params.jsonBestallningNum;
        int num
        String total
        total = "";
        boolean ok = true;
        try {
            num = Integer.parseInt(bestNum)
            //Try to get hold of file and extract text..
            if (urlString.equals("")) {
                ok = false;
                total += message(code: "bestallning.error.url") + "\n"
            }
            if (pw.equals("")) {
                ok = false;
                total += message(code: "bestallning.error.pw") + "\n"
            }
            if (cert.equals("")) {
                ok = false;
                total += message(code: "bestallning.error.cert") + "\n"
            }
            if (ok) {
                try {
                    SSLContext ctx = SSLContext.getInstance("TLS");
                    File f = new File(System.getenv("TAK_HOME") + "/tak/security/" + cert);
                    if (!f.exists()) {
                        total += message(code: "bestallning.error.fileNotFound") + "\n"
                    } else {
                        //TrustManager[] trustManagers = getTrustManagers("jks", new FileInputStream(f), "password");
                        KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(f), pw);
                        ctx.init(keyManagers, null, new SecureRandom());
                        SSLContext.setDefault(ctx);

                        urlString = urlString + num;
                        URL url = new URL(urlString);

                        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
                        con.setAllowUserInteraction(true);

                        InputStream stream = (InputStream) con.getContent();
                        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                        String str;
                        while ((str = br.readLine()) != null) {
                            total += str + "\n";
                        }
                        br.close();
                        if (total != null && total.indexOf("{") != -1) {
                            total = total.substring(total.indexOf("{"), total.lastIndexOf("}") + 1)
                        } else {
                            total = message(code: "bestallning.error.jsonfile.missing")
                        }
                    }
                } catch (Exception e) {
                    log.error("ERROR when trying to get json-file from configured site.\n" + e.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            total = message(code: "bestallning.error.numberformat") + "\n"
            log.error("ERROR when parsing number:" + bestNum + ".\n" + e.getMessage())
        }
        render(view: 'create', model: [jsonBestallningText: total])
    }

    protected static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());
        return kmf.getKeyManagers();
    }

    /*protected static TrustManager[] getTrustManagers(String trustStoreType, InputStream trustStoreFile, String trustStorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType);
        trustStore.load(trustStoreFile, trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }*/

    def createvalidate() {
        сlearFlashMessages()
        def jsonBestallning = params.jsonBestallningText;
        try {
            JsonBestallning bestallning = bestallningService.createOrderObject(jsonBestallning)
            bestallningService.validateOrderObjects(bestallning)

            if (bestallning.getBestallningErrors().size() > 0) {
                StringBuilder stringBuffer = new StringBuilder();
                for (String error : bestallning.getBestallningErrors()) {
                    stringBuffer.append(error).append("<br/>");
                }
                flash.message = stringBuffer.toString();
                render(view: 'create', model: [jsonBestallningText: jsonBestallning])
                return
            }

            if (bestallning.getBestallningInfo().size() > 0) {
                StringBuilder stringBuffer = new StringBuilder();
                stringBuffer.append("<p style=\"margin-left:3em;\">" + message(code: "bestallning.error.saknas.objekt")).append("<br/>");
                for (String info : bestallning.getBestallningInfo()) {
                    stringBuffer.append(info).append("<br/>");
                }
                stringBuffer.append("</p>");
                flash.message = stringBuffer.toString();
            }

            def session = RequestContextHolder.currentRequestAttributes().getSession()
            session.bestallning = bestallning
            render(view: 'bekrafta', model: [bestallning: bestallning, jsonBestallningText: jsonBestallning])
        } catch (Exception e) {
            log.error("Exception when VALIDATEing json-object:\n" + e.getCause().message)
            flash.message = i18nService.msg("bestallning.error.validating", [e.getCause().message])
            render(view: 'create', model: [jsonBestallningText: jsonBestallning])
        }
    }


    def saveOrder() {
        сlearFlashMessages()
        def jsonBestallning = params.jsonBestallningText;
        try {
            def session = RequestContextHolder.currentRequestAttributes().getSession()
            JsonBestallning bestallning = session.bestallning
            bestallningService.executeOrder(bestallning)
            String report = bestallningService.createTextReport(bestallning)
            render(view: 'savedOrderInfo', model: [report: report])
        } catch (Exception e) {
            log.error("Exception when SAVEing json-object:\n" + e.getMessage())
            flash.message = i18nService.msg("bestallning.error.saving", [e.getMessage()])
            render(view: 'create', model: [jsonBestallningText: jsonBestallning])
        }
    }

    def decline() {
        сlearFlashMessages()
        render(view: 'create')
    }

    def сlearFlashMessages() {
        flash.message = ""
    }
}