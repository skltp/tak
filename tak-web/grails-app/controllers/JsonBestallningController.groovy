import org.apache.commons.logging.LogFactory
import se.skltp.tak.web.jsonBestallning.JsonBestallning
import tak.web.BestallningService

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

    BestallningService bestallningService;

    def create() {
        render(view: 'create')
    }

    def createvalidate() {
        сlearFlashMessages()

        def jsonBestallning = params.jsonBestallningTextArea;
        try {
            JsonBestallning bestallning = bestallningService.createOrderObject(jsonBestallning)
            bestallningService.validateOrderObjects(bestallning)

            if (bestallning.getBestallningErrors().size() > 0) {
                StringBuilder stringBuffer = new StringBuilder();
                for (String error : bestallning.getBestallningErrors()) {
                    stringBuffer.append(error).append("<br/>");
                }
                flash.message = stringBuffer.toString();
                render(view: 'create', model: [jsonBestallningTextArea: jsonBestallning])
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

            flash.bestallning = bestallning
            render(view: 'bekrafta', model: [bestallning: bestallning])
        } catch (Exception e) {
            log.error("Exception when VALIDATEing json-object:\n" + e.printStackTrace())
            flash.message = message(code: "bestallning.error.validating")
            render(view: 'create', model: [jsonBestallningTextArea: jsonBestallning])
        }
    }

    def saveOrder() {
        try {
            JsonBestallning bestallning = flash.bestallning
            bestallningService.executeOrder(bestallning)
            render(view: 'savedOrderInfo', model: [bestallning: bestallning])
        } catch (Exception e) {
            log.error("Exception when SAVEing json-object:\n" + e.printStackTrace())
            flash.message = message(code: "bestallning.error.saving")
            render(view: 'create')
        }
    }

    def decline() {
        сlearFlashMessages()
        render(view: 'create')
    }

    def сlearFlashMessages(){
        flash.message = ""
    }
}
