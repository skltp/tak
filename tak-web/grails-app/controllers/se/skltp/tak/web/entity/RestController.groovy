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
                if (jsonString.contains("bestallningsTidpunkt")) {
                    jsonString = insertPlus(jsonString, "bestallningsTidpunkt")
                }
                if (jsonString.contains("genomforandeTidpunkt")) {
                    jsonString = insertPlus(jsonString, "genomforandeTidpunkt")
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
                    for (String info : bestallning.getBestallningInfo()) {
                        stringBuffer.append(info).append("\n");
                    }
                }
                bestallning = bestallningService.executeOrder(bestallning)
                System.out.println("JsonBestallning executed...")
                return "OK:::\n" + stringBuffer.toString() + bestallning.toString()

            } else {
                for (String error : bestallning.getBestallningErrors()) {
                    stringBuffer.append(error).append("\n");
                }
                return "Errors:::\n" + stringBuffer.toString()
            }
        }
    }

    def index() {
        render "index called"
    }

    private String insertPlus(String s, String find) {
        String[] parts = s.split(find);
        String s2 = parts[1];
        int i = s2.indexOf("T");
        String sub = s2.substring(0, i);
        String sub2 = s2.substring(i + 1);
        String sub3 = sub2.substring(0, 8);
        String sub4 = sub2.substring(9);
        String total = parts[0] + find + sub + "T" + sub3 + "+" + sub4;
        return total;
    }

    Object edit() {
        System.out.println("Inside edit() !!!!!!!!!!!!!!!!!!!!!!!!!!")
        render "Get allt"
        print "Get allt"
        return "Get allt"
    }

    Object post() {
        System.out.println("Inside post() !!!!!!!!!!!!!!!!!!!!!!!!!!")
        render "Post allt"
        print "Post allt"
        return "Post allt"
    }
}
