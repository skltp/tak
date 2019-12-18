package se.skltp.tak


import org.apache.commons.lang.math.NumberUtils
import org.apache.commons.logging.LogFactory
import org.springframework.web.context.request.RequestContextHolder
import se.skltp.tak.web.jsonBestallning.BestallningsData
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import tak.web.I18nService
import tak.web.jsonBestallning.BestallningService
import tak.web.jsonBestallning.BestallningStodetConnectionService
import tak.web.jsonBestallning.ReportService

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
        flash.configError = bsConnectionService.validateConnectionConfig()
        log.error("Configuration error. " + flash.configError)
        render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
    }

    /**
     * If user has configured a valid certificate (see tak-web-config.properties), then it should be possible to get the json-file directly from
     * the provider, and to display the content in the web page, for validation.
     */
    def load() {
        сlearFlashMessages()

        String configErrors = bsConnectionService.validateConnectionConfig()
        if (!configErrors.isEmpty()) {
            flash.configError = configErrors
            log.error("Configuration error. " + flash.configError)
            render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
            return
        }

        String jsonBestallning = ""

        String strNum = params.jsonBestallningNum
        if (strNum == null || strNum.isEmpty() || NumberUtils.toLong(strNum, -1) == -1) {
            log.error("Error when parsing bestallning number:" + strNum + ".\n")

            flash.loadError = i18nService.msg("bestallning.error.numberformat", [strNum])
            render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
            return
        }


        long num = NumberUtils.toLong(strNum)
        try {
            jsonBestallning = bsConnectionService.getJsonBestallningFromBS(num)

            if (bsConnectionService.simpleValidateFormat(jsonBestallning)) {
                jsonBestallning = bsConnectionService.validateAndFormat(jsonBestallning)
            } else {
                flash.loadError = i18nService.msg("bestallning.error.simplevalidating", [jsonBestallning])
                log.error("Error when trying to parse json-file from configured site.\n" + jsonBestallning)
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
            flash.loadError = i18nService.msg("bestallning.error.common", [e.getMessage()])
            log.error("ERROR when trying to load/parse json-file from configured site.\n" + e.getMessage())
        }

        render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
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
            flash.configError = bsConnectionService.validateConnectionConfig()
            render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
            return
        }

        try {
            BestallningsData data = bestallningService.prepareOrder(bestallning)

            if (data.getBestallningErrors().size() > 0) {
                flash.error = generateErrorMessage(data)
                log.error("Exception when VALIDATEing json-object:\n" + flash.error)
                flash.configError = bsConnectionService.validateConnectionConfig()
                render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
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
            flash.configError = bsConnectionService.validateConnectionConfig()
            flash.error = i18nService.msg("bestallning.error.validating", [e.getMessage()])
            render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
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
            render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn(), jsonBestallningText: jsonBestallning])
        }
    }

    def decline() {
        сlearFlashMessages()
        render(view: 'createPage', model: [jsonbestallningOn: bsConnectionService.isJsonBestallningOn()])
    }

    def сlearFlashMessages() {
        flash.error = ""
        flash.loadError = ""
        flash.configError = ""
    }
}