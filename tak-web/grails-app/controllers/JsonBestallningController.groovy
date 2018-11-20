import org.apache.commons.logging.LogFactory
import org.springframework.web.context.request.RequestContextHolder
import se.skltp.tak.web.jsonBestallning.AnropsbehorighetBestallning
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import se.skltp.tak.web.jsonBestallning.LogiskadressBestallning
import se.skltp.tak.web.jsonBestallning.TjanstekomponentBestallning
import se.skltp.tak.web.jsonBestallning.TjanstekontraktBestallning
import se.skltp.tak.web.jsonBestallning.VagvalBestallning
import tak.web.BestallningService
import tak.web.I18nService


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
        render(view: 'create')
    }

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
                stringBuffer.append("<p style=\"margin-left:3em;\">" + message(code: "bestallning.problem")).append("<br/>");
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