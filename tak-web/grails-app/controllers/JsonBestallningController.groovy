import se.skltp.tak.web.jsonBestallning.JsonBestallning

import org.apache.commons.logging.LogFactory
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
    def index() { }

    def BestallningService bestallningService;

    def create() {
        render (view:'create')
    }

    def createvalidate() {
        def jsonBestallning = params.jsonBestallningTextArea;
        println(jsonBestallning)
        try {
            JsonBestallning bestallning = bestallningService.createOrderObject(jsonBestallning)
            bestallningService.validateOrderObjects(bestallning)

            if(!bestallning.isValidBestallning()) {
                StringBuilder stringBuffer = new StringBuilder();
                for(String error:bestallning.getBestallningErrors()) {
                    stringBuffer.append(error).append("<br/>");
                }
                flash.message = stringBuffer.toString();
                render (view:'create', model:[jsonBestallningValue:jsonBestallning])
                return
            }
            flash.bestallning = bestallning
            render (view:'bekrafta', model:[bestallning:bestallning])
        } catch (Exception e) {
            log.error(e)
            e.printStackTrace()
            flash.message = message(code: e.message)
            render (view:'create', model:[jsonBestallningValue:jsonBestallning])
        }
    }

    def saveOrder()  {
        try {
            bestallningService.executeOrder(flash.bestallning)
        } catch (Exception e) {
            flash.message = message(code: e.message)
        }
    }
}
