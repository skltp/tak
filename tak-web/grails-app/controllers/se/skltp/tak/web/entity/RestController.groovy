package se.skltp.tak.web.entity

import groovy.json.JsonException
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

    def create() {
        try {
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
            }
            if (jsonString != null) {
                JsonBestallning bestallning = bestallningService.createOrderObject(jsonString)
                log.info("JsonBestallning created:::" + bestallning.genomforandeTidpunkt + " Utforare=" + bestallning.getUtforare())
                bestallningService.validateOrderObjects(bestallning)
                log.info("JsonBestallning validated:::" + bestallning.genomforandeTidpunkt + " Utforare=" + bestallning.getUtforare())
                StringBuilder stringBuffer = new StringBuilder();
                if (bestallning.getBestallningErrors().isEmpty()) {
                    bestallningService.executeOrder(bestallning)
                    log.info("JsonBestallning executed::: GenomforandeTidpunkt=" + bestallning.genomforandeTidpunkt + " Utforare=" + bestallning.getUtforare())

                } else {
                    log.error("JsonBestallning ERROR::: GenomforandeTidpunkt=" + bestallning.getGenomforandeTidpunkt() + " Utforare=" + bestallning.getUtforare() + "\n" + responseString)
                }
                stringBuffer.append(bestallningService.createTextReport(bestallning))
                response.setCharacterEncoding("UTF-8")
                response.outputStream << stringBuffer.toString()
            } else {
                log.error("JsonBestallning ERROR::: jsonString was NULL or not found in request. File not found?")
                response.outputStream << "JsonBestallning ERROR::: jsonString was NULL or not found in request."
            }
        } catch (java.lang.reflect.UndeclaredThrowableException e) {
            log.error("RUNTIME ERROR:::" + e.getCause())
            response.outputStream << "RUNTIME ERROR:::\n" + e.getCause()
        } catch (JsonException e) {
            log.error("RUNTIME ERROR:::" + e.getCause())
            response.outputStream << "RUNTIME ERROR:::\n" + e.getCause()
        } catch (Exception e) {
            log.error("RUNTIME ERROR:::" + e.getCause())
            response.outputStream << "RUNTIME ERROR:::\n" + e.getMessage()
        }
    }

    /**
     * Curl (or something else) is removing illegal char '+' from content in json file..
     * @param s The whole string
     * @param find The date to fix
     * @return Same string with the '+' inserted.
     */
    private String insertPlus(String s, String find) {
        String[] parts = s.split(find)
        String s2 = parts[1]
        int i = s2.indexOf("T")
        String sub = s2.substring(0, i + 9)
        String sub2 = s2.substring(24)
        String total = parts[0] + find + sub + "+" + sub2
        return total
    }
}
