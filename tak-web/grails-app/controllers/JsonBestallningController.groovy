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
                stringBuffer.append("<p style=\"margin-left:3em;\">" + message(code: "bestallning.error.saknas.objekt")).append("<br/>");
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
            log.error("Exception when VALIDATEing json-object:\n" + e.getMessage())
            flash.message = message(code: "bestallning.error.validating")
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
            render(view: 'savedOrderInfo', model: [report: createTextReport(bestallning)])
        } catch (Exception e) {
            log.error("Exception when SAVEing json-object:\n" + e.getMessage())
            flash.message = i18nService.msg("bestallning.error.saving", [e.getMessage()])
            render(view: 'create', model: [jsonBestallningText: jsonBestallning])
        }
    }

    private createTextReport(JsonBestallning bestallning) {
        LinkedList<String> newObjects = new LinkedList<String>();
        LinkedList<String> updatedObjects = new LinkedList<String>();
        List<String> deletedObjects = new LinkedList<String>();
        LinkedList<String> nonDeletedObjects = new LinkedList<String>();

        fillListsWithDeletedObjects(bestallning, deletedObjects, nonDeletedObjects);
        fillListsWithCreatedOrUpdatedObjects(bestallning, newObjects, updatedObjects);



        StringBuffer report = new StringBuffer();
        report.
                append("Platform: ").append(bestallning.plattform).append("\n").
                append("Format Version: ").append(bestallning.formatVersion).append("\n").
                append("Version: ").append(bestallning.version).append("\n").
                append("BestallningsTidpunkt: ").append(bestallning.bestallningsTidpunkt).append("\n").
                append("GenomforandeTidpunkt: ").append(bestallning.genomforandeTidpunkt).append("\n").
                append("Utforare: ").append(bestallning.utforare).append("\n").
                append("Kommentar: ").append(bestallning.kommentar).append("\n");

        report.append("\n").append("Nyligen skapad: \n");
        for (String text : newObjects) {
            report.append(text).append("\n");
        }

        if(newObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Nyligen uppdaterad: \n");
        for (String text : updatedObjects) {
            report.append(text).append("\n");
        }
        if(updatedObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Nyligen borttagen: \n");
        for (String text : deletedObjects) {
            report.append(text).append("\n");
        }
        if(deletedObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Ej existerande för borttagning: \n");
        for (String text : nonDeletedObjects) {
            report.append(text).append("\n");
        }
        if(nonDeletedObjects.size() == 0) report.append("-").append("\n");

        return report.toString()
    }

    private void fillListsWithDeletedObjects(JsonBestallning bestallning, List deletedObjects, List nonDeletedObjects) {
        for (AnropsbehorighetBestallning element : bestallning.exkludera.getAnropsbehorigheter()) {
            if (element.anropsbehorighet != null) {
                deletedObjects.add("Anropsbehorighet: " + element.toString())
            } else {
                nonDeletedObjects.add("Anropsbehorighet: " + element.toString())
            }
        }

        for (VagvalBestallning element : bestallning.exkludera.getVagval()) {
            if (element.vagval != null) {
                deletedObjects.add("Vagval: " + element.toString())
            } else {
                nonDeletedObjects.add("Vagval: " + element.toString())
            }
        }
    }

    private void fillListsWithCreatedOrUpdatedObjects(JsonBestallning bestallning, List newObjects, List updatedObjects) {
        for (LogiskadressBestallning element : bestallning.inkludera.getLogiskadresser()) {
            if (element.getLogiskAdress().getVersion() == 0) {
                newObjects.add("Logiskadress: " + element.toString())
            } else {
                updatedObjects.add("Logiskadress: " + element.toString())
            }
        }
        for (TjanstekontraktBestallning element : bestallning.inkludera.getTjanstekontrakt()) {
            if (element.getTjanstekontrakt().getVersion() == 0) {
                newObjects.add("Tjanstekontrakt: " + element.toString())
            } else {
                updatedObjects.add("Tjanstekontrakt: " + element.toString())
            }
        }
        for (TjanstekomponentBestallning element : bestallning.inkludera.getTjanstekomponenter()) {
            if (element.getTjanstekomponent().getVersion() == 0) {
                newObjects.add("Tjanstekomponent: " + element.toString())
            } else {
                updatedObjects.add("Tjanstekomponent: " + element.toString())
            }
        }

        for (AnropsbehorighetBestallning element : bestallning.inkludera.getAnropsbehorigheter()) {
            newObjects.add("Anropsbehorighet: " + element.toString())

        }

        for (VagvalBestallning element : bestallning.inkludera.getVagval()) {
            newObjects.add("Vagval: " + element.toString())
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