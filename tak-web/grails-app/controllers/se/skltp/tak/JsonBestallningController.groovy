package se.skltp.tak

import com.fasterxml.jackson.databind.ObjectMapper
import se.skltp.tak.web.jsonBestallning.BestallningsData
import tak.web.jsonBestallning.BestallningService
import tak.web.jsonBestallning.ReportService
import org.apache.commons.logging.LogFactory
import org.springframework.web.context.request.RequestContextHolder
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import tak.web.I18nService

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
    ReportService reportService

    String validateConfig() {
        String configErrors = ""
        //Before rendering view create, check if user has configured to get 'bestallning' from provider by number.
        if (isUrlConfigured()) {
            if (grailsApplication.config.tak.bestallning.pw?.isEmpty()) {
                configErrors += message(code: "bestallning.error.pw") + "\n"
            }
            if (grailsApplication.config.tak.bestallning.cert?.isEmpty()) {
                configErrors += message(code: "bestallning.error.cert") + "\n"
            }
            if (grailsApplication.config.tak.bestallning.serverCert?.isEmpty()) {
                configErrors += message(code: "bestallning.error.serverpw") + "\n"
            }
            if (grailsApplication.config.tak.bestallning.serverPw?.isEmpty()) {
                configErrors += message(code: "bestallning.error.servercert") + "\n"
            }
        }
        configErrors
    }

    private boolean isUrlConfigured() {
        !grailsApplication.config.tak.bestallning.url?.isEmpty()
    }

    def create() {
        def jsonBestallning = params.jsonBestallningText
        flash.message = validateConfig()
        render(view: 'create', model: [isUrlConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
    }

    /**
     * If user has configured a valid certificate (see tak-web-config.properties), then it should be possible to get the json-file directly from
     * the provider, and to display the content in the web page, for validation.
     */
    def loadcreate() {
        def jsonBestallning = ""; //params.jsonBestallningText

        String errors = validateConfig()
        if(!errors.isEmpty()){
            flash.message = errors
            render(view: 'create', model: [isUrlConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
            return
        }

        String urlString = grailsApplication.config.tak.bestallning.url
        String pw = grailsApplication.config.tak.bestallning.pw
        String cert = grailsApplication.config.tak.bestallning.cert
        String serverCert = grailsApplication.config.tak.bestallning.serverCert
        String serverPw = grailsApplication.config.tak.bestallning.serverPw

        def bestNum = params.jsonBestallningNum

        if (!bestNum?.isEmpty()) {
            try {
                int num = Long.parseLong(bestNum)
                    try {
                        SSLContext ctx = SSLContext.getInstance("TLS")
                        File f = new File(System.getenv("TAK_HOME") + "/security/" + cert)
                        File f2 = new File(System.getenv("TAK_HOME") + "/security/" + serverCert)
                        if (!f.exists() || !f2.exists()) {
                            jsonBestallning += message(code: "bestallning.error.fileNotFound") + "\n"
                        } else {
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
                            if (jsonBestallning != null && jsonBestallning.indexOf("{") != -1 && jsonBestallning.indexOf("}") != -1) {
                                jsonBestallning = jsonBestallning.substring(jsonBestallning.indexOf("{"), jsonBestallning.lastIndexOf("}") + 1)
                                ObjectMapper mapper = new ObjectMapper()
                                JsonBestallning json = mapper.readValue(jsonBestallning, JsonBestallning.class)
                                jsonBestallning = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)
                            } else {
                                jsonBestallning = message(code: "bestallning.error.jsonfile.missing")
                            }
                        }
                    } catch (ConnectException e) {
                        jsonBestallning = message(code: "bestallning.error.connect.failure")
                        log.error("ERROR when trying to connect to server at configured site.\n" + e.getMessage())
                    }  catch (FileNotFoundException e) {
                        jsonBestallning = message(code: "bestallning.error.jsonfile.missing")
                        log.error("ERROR when trying to get json-file from configured site.\n" + e.getMessage())
                    } catch (Exception e) {
                        jsonBestallning = message(code: "bestallning.error.simplevalidating")
                        log.error("ERROR when trying to parse json-file from configured site.\n" + e.getMessage())
                    }
            } catch (NumberFormatException e) {
                jsonBestallning = message(code: "bestallning.error.numberformat") + " Nummer: " + bestNum + "\n"
                log.error("ERROR when parsing number:" + bestNum + ".\n" + e.getMessage())
            }
        }
        render(view: 'create', model: [hasCertConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
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

    def createvalidate() {
        сlearFlashMessages()

        def jsonBestallning = params.jsonBestallningText
        JsonBestallning bestallning

        try {
            bestallning = bestallningService.createOrderObject(jsonBestallning)
        } catch (Exception ex) {
            ex.printStackTrace()
            log.error("Exception when CREATINGing json-object:\n" + ex.cause.message)
            flash.error = i18nService.msg("bestallning.error.create", [ex.cause.message])
            render(view: 'create', model: [hasCertConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
            return
        }

        try {
            BestallningsData data = bestallningService.prepareOrder(bestallning)

            if (data.getBestallningErrors().size() > 0) {
                flash.error = generateErrorMessage(data)
                render(view: 'create', model: [hasCertConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
                return
            }

            if (data.getBestallningInfo().size() > 0) {
                flash.error = generateInfoMessage(data)
            }

            def session = RequestContextHolder.currentRequestAttributes().getSession()
            session.bestallning = data

            render(view: 'bekrafta', model: [bestallning: bestallning, jsonBestallningText: jsonBestallning, bestallningsData:data])
        } catch (Exception e) {
            e.printStackTrace()
            log.error("Exception when VALIDATEing json-object:\n" + e.getMessage())
            flash.error = i18nService.msg("bestallning.error.validating", [e.getMessage()])
            render(view: 'create', model: [hasCertConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
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
            render(view: 'savedOrderInfo', model: [report: report])
        } catch (Exception e) {
            e.printStackTrace()
            log.error("Exception when SAVEing json-object:\n" + e.getMessage())
            flash.error = i18nService.msg("bestallning.error.saving", [e.getMessage()])
            render(view: 'create', model: [hasCertConfigured: isUrlConfigured(), jsonBestallningText: jsonBestallning])
        }
    }

    def decline() {
        сlearFlashMessages()
        render(view: 'create', model: [hasCertConfigured: isUrlConfigured()])
    }

    def сlearFlashMessages() {
        flash.error = ""
        flash.message = ""
    }
}