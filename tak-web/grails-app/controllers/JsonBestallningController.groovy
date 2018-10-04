import se.skltp.tak.web.jsonBestallning.JsonBestallning
import se.skltp.tak.web.jsonBestallning.JsonBestallningCreator
import org.apache.commons.logging.LogFactory

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

    def create() {
        render (view:'create')
    }

    def createvalidate() {
        def jsonBestallning = params.jsonBestallningTextArea;
        println(jsonBestallning)
        try {
            JsonBestallning bestallning = JsonBestallningCreator.createBestallningObject(jsonBestallning)
            confirmOrder(bestallning)
        } catch (Exception e1) {
            flash.message = message(code: e1.message)
            redirect(action: "create")
        }
    }

    def confirmOrder(JsonBestallning bestallning){
        JsonBestallningCreator.findAllOrderObjects(bestallning)
        render (view:'bekrafta', model:[bestallning:bestallning])
    }
}
