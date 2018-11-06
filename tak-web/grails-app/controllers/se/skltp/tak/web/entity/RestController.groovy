package se.skltp.tak.web.entity

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



class RestController {

    private static final log = LogFactory.getLog(this)

    BestallningService bestallningService;

    String responseString

    String getResponseString() {
        return responseString
    }

    def create() {
        //{plattform:SLL-PROD,formatVersion:1.0,version:1,bestallningsTidpunkt:2018-10-09T10:23:10+0200}
        System.out.println("Inside create()!")
        String jsonString;
        Set entries = params.entrySet()
        for (String s : entries) {
            if (s.startsWith("{") || s.startsWith("\"{") || s.startsWith("quot;{")) {
                jsonString = s
                jsonString = jsonString.replace("=", "")
                jsonString = jsonString.replace("quot;", "")
                jsonString = jsonString.replace("\t", "")
                if (jsonString.indexOf("+") < 0) {
                    if (jsonString.contains("bestallningsTidpunkt")) {
                        jsonString = insertPlus(jsonString, "bestallningsTidpunkt")
                    }
                    if (jsonString.contains("genomforandeTidpunkt")) {
                        jsonString = insertPlus(jsonString, "genomforandeTidpunkt")
                    }
                }
                break
            }
            System.out.println("PARAM:" + s)
        }
        if (jsonString != null) {
            JsonBestallning bestallning = bestallningService.createOrderObject(jsonString)
            System.out.println("JsonBestallning created...")
            bestallningService.validateOrderObjects(bestallning)
            System.out.println("JsonBestallning validated...")
            StringBuilder stringBuffer = new StringBuilder();
            if (bestallning.getBestallningErrors().isEmpty()) {
                if (bestallning.getBestallningInfo().size() > 0) {
                    stringBuffer.append(message(code: "bestallning.error.saknas.objekt")) + "<br/>"
                    for (String info : bestallning.getBestallningInfo()) {
                        stringBuffer.append(info).append("\n");
                    }
                }
                bestallningService.executeOrder(bestallning)
                System.out.println("JsonBestallning executed...")
                responseString = stringBuffer.toString()
                response.outputStream << responseString
                log.info("JsonBestallning executed::: GenomforandeTidpunkt=" + bestallning.genomforandeTidpunkt + " Utforare=" + bestallning.getUtforare())

            } else {
                for (String error : bestallning.getBestallningErrors()) {
                    stringBuffer.append(error).append("\n");
                }
                responseString = stringBuffer.toString()
                response.outputStream << responseString
                log.error("JsonBestallning ERROR::: GenomforandeTidpunkt=" + bestallning.getGenomforandeTidpunkt() + " Utforare=" + bestallning.getUtforare() + "\n" + responseString)
            }
        } else {
            log.error("JsonBestallning ERROR::: jsonString was NULL or not found in request.")
        }
    }

    /**
     * Curl (or something else) is removing illegal char '+' from content in json file..
     * @param s The whole string
     * @param find The date to fix
     * @return Same string with the '+' inserted.
     */
    private String insertPlus(String s, String find) {
        String[] parts = s.split(find);
        String s2 = parts[1];
        int i = s2.indexOf("T");
        String sub = s2.substring(0, i + 9);
        String sub2 = s2.substring(24);
        String total = parts[0] + find + sub + "+" + sub2;
        return total;
    }
}
