package se.skltp.tak

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.logging.LogFactory
import org.springframework.web.context.request.RequestContextHolder
import se.skltp.tak.web.entity.TAKSettings
import se.skltp.tak.web.jsonBestallning.BestallningsData
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import tak.web.I18nService
import tak.web.alerter.PubliceringMailAlerterService
import tak.web.jsonBestallning.BestallningService
import tak.web.jsonBestallning.BestallningStodetConnectionService
import tak.web.jsonBestallning.ReportService

import javax.net.ssl.*
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

    I18nService i18nService
    BestallningService bestallningService
    BestallningStodetConnectionService bsConnectionService
    ReportService reportService



    def createPage() {
        def jsonBestallning = params.jsonBestallningText
        String configErrors = bsConnectionService.validateConnectionConfig()
        flash.configError = configErrors
        render(view: 'createPage', model: [jsonbestallningOn: isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
    }



    private boolean isJsonBestallningOn() {
        if(grailsApplication.config.tak.bestallning.on.size() != 0 && Boolean.parseBoolean(grailsApplication.config.tak.bestallning.on)){
            return true
        } else {
            flash.info = message(code: 'bestallning.off')
            return false
        }
    }

    /**
     * If user has configured a valid certificate (see tak-web-config.properties), then it should be possible to get the json-file directly from
     * the provider, and to display the content in the web page, for validation.
     */
    def load() {
        сlearFlashMessages()
        def jsonBestallning = "";

        String configErrors = bsConnectionService.validateConnectionConfig()
        if (!configErrors.isEmpty()) {
            createPage()
            return
        }

        String urlString = grailsApplication.config.tak.bestallning.url
        String pw = grailsApplication.config.tak.bestallning.pw
        String cert = grailsApplication.config.tak.bestallning.cert
        String serverCert = grailsApplication.config.tak.bestallning.serverCert
        String serverPw = grailsApplication.config.tak.bestallning.serverPw

        File f = new File(System.getenv("TAK_HOME") + "/security/" + cert)
        File f2 = new File(System.getenv("TAK_HOME") + "/security/" + serverCert)

        String bestNum = params.jsonBestallningNum
        bestNum = bestNum == null ? "" : bestNum.trim()
        if (!bestNum?.isEmpty()) {
            try {
                int num = Long.parseLong(bestNum)
                try {
                    SSLContext ctx = SSLContext.getInstance("TLS")
                    KeyManager[] keyManagers = getKeyManagers("pkcs12", new FileInputStream(f), pw)
                    TrustManager[] trustManagers = getTrustManagers("jks", new FileInputStream(f2), serverPw)
                    ctx.init(keyManagers, trustManagers, new SecureRandom())
                    SSLContext.setDefault(ctx)

                    urlString = urlString + num
                    URL url = new URL(urlString)
                    HttpsURLConnection con = (HttpsURLConnection) url.openConnection()

                    InputStream stream = (InputStream) con.getContent()
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))
                    String str
                    while ((str = br.readLine()) != null) {
                        jsonBestallning += str + "\n"
                    }
                    br.close()
                    if (jsonBestallning != null && jsonBestallning.contains("{") && jsonBestallning.contains("}")) {
                        jsonBestallning = jsonBestallning.substring(jsonBestallning.indexOf("{"), jsonBestallning.lastIndexOf("}") + 1)
                        ObjectMapper mapper = new ObjectMapper()
                        JsonBestallning json = mapper.readValue(jsonBestallning, JsonBestallning.class)
                        jsonBestallning = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)

                    } else {
                        flash.loadError = i18nService.msg("bestallning.error.simplevalidating", [jsonBestallning])
                        log.error("ERROR when trying to parse json-file from configured site.\n" + jsonBestallning)
                    }

                } catch (ConnectException e) {
                    flash.loadError = i18nService.msg("bestallning.error.connect.failure", [e.getMessage()])
                    log.error("ERROR when trying to connect to server at configured site.\n" + e.getMessage())
                } catch (FileNotFoundException e) {
                    flash.loadError = i18nService.msg("bestallning.error.jsonfile.missing")
                    log.error("ERROR when trying to get json-file from configured site.\n" + e.getMessage())
                } catch (IOException e) {
                    flash.loadError = i18nService.msg("bestallning.error.ioexception", [e.getMessage()])
                    log.error("IO ERROR when getting file from configured site.\n" + e.getMessage())
                } catch (Exception e) {
                    flash.loadError = i18nService.msg("bestallning.error.simplevalidating", [e.getMessage()])
                    log.error("ERROR when trying to parse json-file from configured site.\n" + e.getMessage())
                }
            } catch (NumberFormatException e) {
                flash.loadError = i18nService.msg("bestallning.error.numberformat", [bestNum])
                log.error("ERROR when parsing number:" + bestNum + ".\n" + e.getMessage())
            }
        }

     render(view: 'createPage', model: [jsonbestallningOn: isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
    }


    protected
    static KeyManager[] getKeyManagers(String keyStoreType, InputStream keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(keyStoreFile, keyStorePassword.toCharArray())
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(keyStore, keyStorePassword.toCharArray())
        return kmf.getKeyManagers()
    }

    protected
    static TrustManager[] getTrustManagers(String trustStoreType, InputStream trustStoreFile, String trustStorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType)
        trustStore.load(trustStoreFile, trustStorePassword.toCharArray())
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(trustStore)
        return tmf.getTrustManagers()
    }

    def validate() {
        сlearFlashMessages()

        def jsonBestallning = params.jsonBestallningText
        JsonBestallning bestallning

        try {
            bestallning = bestallningService.createOrderObject(jsonBestallning)
        } catch (Exception ex) {
            ex.printStackTrace()
            log.error("Exception when CREATing json-object:\n" + ex.cause.message)
            flash.error = i18nService.msg("bestallning.error.create", [ex.cause.message])
            createPage()
            render(view: 'createPage', model: [jsonbestallningOn: isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
            return
        }

        try {
            BestallningsData data = bestallningService.prepareOrder(bestallning)

            if (data.getBestallningErrors().size() > 0) {
                flash.error = generateErrorMessage(data)
                render(view: 'createPage', model: [isUrlConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
                return
            }

            if (data.getBestallningInfo().size() > 0) {
                flash.error = generateInfoMessage(data)
            }

            def session = RequestContextHolder.currentRequestAttributes().getSession()
            session.bestallning = data

            render(view: 'confirmPage', model: [bestallning: bestallning, jsonBestallningText: jsonBestallning, bestallningsData: data])
        } catch (Exception e) {
            e.printStackTrace()
            log.error("Exception when VALIDATEing json-object:\n" + e.getMessage())
            flash.error = i18nService.msg("bestallning.error.validating", [e.getMessage()])
            render(view: 'createPage', model: [jsonbestallningOn: isJsonBestallningOn(), jsonBestallningText: jsonBestallning])

        }
    }

    private String generateInfoMessage(BestallningsData data) {
        StringBuilder stringBuffer = new StringBuilder()
        stringBuffer.append("<p style=\"margin-left:3em;\">" + message(code: "bestallning.problem")).append("<br/>")
        for (String info : data.getBestallningInfo()) {
            stringBuffer.append(info).append("<br/>")
        }
        stringBuffer.append("</p>")
        stringBuffer.toString()
    }

    private String generateErrorMessage(BestallningsData data) {
        StringBuilder stringBuffer = new StringBuilder()
        for (String error : data.getBestallningErrors()) {
            stringBuffer.append(error).append("<br/>")
        }
        stringBuffer.toString()
    }


    def saveOrder() {
        сlearFlashMessages()
        def jsonBestallning = params.jsonBestallningText
        try {
            def session = RequestContextHolder.currentRequestAttributes().getSession()
            BestallningsData data = session.bestallning
            String report = reportService.createNewReport(data)
            bestallningService.executeOrder(data)
            render(view: 'savedOrderInfoPage', model: [report: report])
        } catch (Exception e) {
            e.printStackTrace()
            log.error("Exception when SAVEing json-object:\n" + e.getMessage())
            flash.error = i18nService.msg("bestallning.error.saving", [e.getMessage()])
            render(view: 'createPage', model: [isUrlConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
        }
    }

    def decline() {
        сlearFlashMessages()
        render(view: 'createPage', model: [isUrlConfigured: isUrlConfigured()])
    }

    def сlearFlashMessages() {
        flash.error = ""
        flash.message = ""
        flash.loadError = ""
        flash.configError = ""
    }
}