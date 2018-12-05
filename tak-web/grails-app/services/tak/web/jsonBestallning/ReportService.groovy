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

package tak.web.jsonBestallning

import se.skltp.tak.core.entity.*
import se.skltp.tak.web.jsonBestallning.*

class ReportService {
    private String dateFormat = "yyyy-MM-dd'T'hh:mm:ssZ"

    String createTextReport(JsonBestallning bestallning) {
        ReportData data = new ReportData()

        fillListsWithDeletedObjects(bestallning, data);
        fillListsWithCreatedOrUpdatedObjects(bestallning, data);

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
        for (String text : data.newObjects) {
            report.append(text).append("\n");
        }

        if (data.newObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Nyligen uppdaterad: \n");
        for (String text : data.updatedObjects) {
            report.append(text).append("\n");
        }
        if (data.updatedObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Nyligen borttagen: \n");
        for (String text : data.deletedObjects) {
            report.append(text).append("\n");
        }
        if (data.deletedObjects.size() == 0) report.append("-").append("\n");

        report.append("\n").append("Ej existerande för borttagning: \n");
        for (String text : data.nonDeletedObjects) {
            report.append(text).append("\n");
        }
        if (data.nonDeletedObjects.size() == 0) report.append("-").append("\n");

        return report.toString()
    }

    private void fillListsWithDeletedObjects(JsonBestallning bestallning, ReportData data) {
        for (AnropsbehorighetBestallning element : bestallning.exkludera.getAnropsbehorigheter()) {
            if (element.getAropsbehorigheterForDelete().size() > 0) {
                element.getAropsbehorigheterForDelete().each() {
                    data.deletedObjects.add(transformToString(it))
                }
            } else {
                data.nonDeletedObjects.add(transformToString(element))
            }
        }

        for (VagvalBestallning element : bestallning.exkludera.getVagval()) {
            if (element.getVagvalForDelete().size() > 0) {
                element.getVagvalForDelete().each() {
                    data.deletedObjects.add(transformToString(it))
                }
            } else {
                data.nonDeletedObjects.add(transformToString(element))
            }
        }
    }

    private void fillListsWithCreatedOrUpdatedObjects(JsonBestallning bestallning, ReportData data) {
        for (LogiskadressBestallning element : bestallning.inkludera.getLogiskadresser()) {
            if (element.isNew()) {
                data.newObjects.add(transformToString(element.logiskAdress))
            } else {
                data.updatedObjects.add(transformToString(element.logiskAdress))
            }
        }
        for (TjanstekontraktBestallning element : bestallning.inkludera.getTjanstekontrakt()) {
            if (element.isNew()) {
                data.newObjects.add(transformToString(element.tjanstekontrakt))
            } else {
                data.updatedObjects.add(transformToString(element.tjanstekontrakt))
            }
        }
        for (TjanstekomponentBestallning element : bestallning.inkludera.getTjanstekomponenter()) {
            if (element.isNew()) {
                data.newObjects.add(transformToString(element.tjanstekomponent))
            } else {
                data.updatedObjects.add(transformToString(element.tjanstekomponent))
            }
        }

        for (AnropsbehorighetBestallning element : bestallning.inkludera.getAnropsbehorigheter()) {
            data.newObjects.add(transformToString(element.newAnropsbehorighet))
            if (element.oldAnropsbehorighet.deleted) {
                data.deletedObjects.add(transformToString(element.oldAnropsbehorighet))
            } else {
                data.updatedObjects.add(transformToString(element.oldAnropsbehorighet))
            }
        }

        for (VagvalBestallning element : bestallning.inkludera.getVagval()) {
            data.newObjects.add(transformToString(element.newVagval))
            if (element.oldVagval.deleted) {
                data.deletedObjects.add(transformToString(element.oldVagval))
            } else {
                data.updatedObjects.add(transformToString(element.oldVagval))
            }
        }
    }

    class ReportData {
        private LinkedList<String> newObjects = new LinkedList<String>();
        private LinkedList<String> updatedObjects = new LinkedList<String>();
        private List<String> deletedObjects = new LinkedList<String>();
        private LinkedList<String> nonDeletedObjects = new LinkedList<String>();

        private addNew(String data) {
            newObjects.add(data)
        }

        private addUpdated(String data) {
            updatedObjects.add(data)
        }

        private addDeleted(String data) {
            deletedObjects.add(data)
        }

        private addNonDeleted(String data) {
            nonDeletedObjects.add(data)
        }

        LinkedList<String> getNewObjects() {
            return newObjects
        }

        LinkedList<String> getUpdatedObjects() {
            return updatedObjects
        }

        List<String> getDeletedObjects() {
            return deletedObjects
        }

        LinkedList<String> getNonDeletedObjects() {
            return nonDeletedObjects
        }
    }

    private String transformToString(LogiskAdress element) {
        return "Logiskadress: " + element.hsaId
    }

    private String transformToString(Tjanstekontrakt element) {
        return "Tjanstekontrakt: " + element.namnrymd
    }

    private String transformToString(Tjanstekomponent element) {
        return "Tjanstekomponent: " + element.hsaId
    }

    private String transformToString(Anropsbehorighet element) {
        return "Anropsbehorighet: " +
                element.logiskAdress.hsaId + " - " +
                element.tjanstekonsument.hsaId + " - " +
                element.tjanstekontrakt.namnrymd + "  " +
                "(" + element.fromTidpunkt + " - " + element.tomTidpunkt + ")"
    }

    private String transformToString(AnropsbehorighetBestallning element) {
        return "Anropsbehorighet(from Beställning): " +
                element.logiskAdress + " - " +
                element.tjanstekonsument + " - " +
                element.tjanstekontrakt

    }

    private String transformToString(Vagval element) {
        return "Vagval: " +
                element.logiskAdress.hsaId + " - " +
                element.tjanstekontrakt.namnrymd + " - " +
                element.anropsAdress.tjanstekomponent.hsaId + " - " +
                element.anropsAdress.rivTaProfil.namn + " - " +
                element.anropsAdress.adress + " " +
                "(" + element.fromTidpunkt + " - " + element.tomTidpunkt + ")"
    }

    private String transformToString(VagvalBestallning element) {
        return "Vagval(from Beställning): " +
                element.logiskAdress + " - " +
                element.tjanstekontrakt + " - " +
                element.tjanstekomponent + " - " +
                element.rivtaprofil + " - " +
                element.adress
    }
}
