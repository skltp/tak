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
import tak.web.I18nService

class ReportService {
    I18nService i18nService

    public enum Status {
        NEW("New"),
        UPDATED("Uppdatera"),
        DELETED("Ta bort"),
        DEACTIVATED("Deakivera"),
        NOT_EXISTS("Existerar ej")

        private Status(String description) {
            this.description = description;
        }

        private String description;

        @Override
        public String toString() {
            return description
        }
    }

    class VagvalStatus {
        private Status newVagvalStatus
        private Status oldVagvalStatus

        Status getNewVagvalStatus() {
            return newVagvalStatus
        }

        Status getOldVagvalStatus() {
            return oldVagvalStatus
        }

        void setNewVagvalStatus(Status newVagvalStatus) {
            this.newVagvalStatus = newVagvalStatus
        }

        void setOldVagvalStatus(Status oldVagvalStatus) {
            this.oldVagvalStatus = oldVagvalStatus
        }
    }

    String createNewReport(BestallningsData data) {
        JsonBestallning bestallning = data.bestallning

        StringBuffer report = new StringBuffer();
        report.
                append("Platform: ").append(bestallning.plattform).append("\n").
                append("Format Version: ").append(bestallning.formatVersion).append("\n").
                append("Version: ").append(bestallning.version).append("\n").
                append("BestallningsTidpunkt: ").append(bestallning.bestallningsTidpunkt).append("\n").
                append("GenomforandeTidpunkt: ").append(bestallning.genomforandeTidpunkt).append("\n").
                append("Utforare: ").append(bestallning.utforare).append("\n").
                append("Kommentar: ").append(bestallning.kommentar).append("\n");


        report.append("\n").append("Inkludera:").append("\n");
        report.append("Logisk adress:").append("\n");
        bestallning.inkludera.logiskadresser.each {
            ReportPair pair = getReportData(it, data)
            report.append(pair.status + " : " + pair.value).append("\n");
        }

        report.append("\n").append("Tjanstekomponent:").append("\n");
        bestallning.inkludera.tjanstekomponenter.each {
            ReportPair pair = getReportData(it, data)
            report.append(pair.status + " : " + pair.value).append("\n");
        }

        report.append("\n").append("Tjanstekontrakt:").append("\n");
        bestallning.inkludera.tjanstekontrakt.each {
            ReportPair pair = getReportData(it, data)
            report.append(pair.status + " : " + pair.value).append("\n");
        }

        report.append("\n").append("Anropsbehorighet:").append("\n");
        bestallning.inkludera.anropsbehorigheter.each {
            ReportPair pair = getReportData(it, data)
            report.append(pair.status + " : " + pair.value).append("\n");
        }

        report.append("\n").append("Vagval:").append("\n");
        bestallning.inkludera.vagval.each {
            List<ReportPair> pair = getReportData(it, data)
            pair.each {
                report.append(it.status + " : " + it.value).append("\n");
            }
        }

        report.append("\n\n").append("Exkludera:").append("\n")
        report.append("\n").append("Anropsbehorighet:").append("\n");
        bestallning.exkludera.anropsbehorigheter.each {
            ReportPair pair = getReportData(it, data)
            report.append(pair.status + " : " + pair.value).append("\n");
        }

        report.append("\n").append("Vagval:").append("\n");
        bestallning.exkludera.vagval.each {
            List<ReportPair> pair = getReportData(it, data)
            pair.each {
                report.append(it.status + " : " + it.value).append("\n");
            }
        }
        return report.toString()

    }


    private Status getBestallningsStatus(LogiskAdress logiskAdress) {
        if (logiskAdress.id == 0l) return Status.NEW
        else return Status.UPDATED
    }

    private Status getBestallningsStatus(Tjanstekontrakt tjanstekontrakt) {
        if (tjanstekontrakt.id == 0l) return Status.NEW
        else return Status.UPDATED
    }

    private Status getBestallningsStatus(Tjanstekomponent tjanstekomponent) {
        if (tjanstekomponent.id == 0l) return Status.NEW
        else return Status.UPDATED
    }

    private Status getAnropsbehorighetBestallningsStatus(Anropsbehorighet anropsbehorighet, Date genomforandeTidpunkt) {
        if (anropsbehorighet == null) return Status.NOT_EXISTS
        if (anropsbehorighet?.id == 0l) return Status.NEW
        if (anropsbehorighet.tomTidpunkt <= genomforandeTidpunkt) return Status.DEACTIVATED
        else return Status.UPDATED
    }

    private VagvalStatus getVagvalBestallningsStatus(BestallningsData.VagvalPair vagval, Date genomforandeTidpunkt) {
        VagvalStatus vagvalStatus = new VagvalStatus()

        if (vagval == null) {
            vagvalStatus.oldVagvalStatus = Status.NOT_EXISTS
            return vagvalStatus
        }

        if (vagval.newVagval != null) {
            vagvalStatus.newVagvalStatus = Status.NEW
        }

        if (vagval.oldVagval != null) {
            if (vagval.oldVagval?.deleted) {
                vagvalStatus.oldVagvalStatus = Status.DELETED
            } else if (vagval.oldVagval?.tomTidpunkt <= genomforandeTidpunkt) {
                vagvalStatus.oldVagvalStatus = Status.DEACTIVATED
            } else {
                vagvalStatus.oldVagvalStatus = Status.UPDATED
            }
        }

        return vagvalStatus
    }

    ReportPair getReportData(TjanstekontraktBestallning bestallning, BestallningsData data) {
        Tjanstekontrakt tjanstekontrakt = data.getTjanstekontrakt(bestallning)
        Status status = getBestallningsStatus(tjanstekontrakt)
        return new ReportPair(status.toString(), tjanstekontrakt.namnrymd)
    }

    ReportPair getReportData(TjanstekomponentBestallning bestallning, BestallningsData data) {
        Tjanstekomponent tjanstekomponent = data.getTjanstekomponent(bestallning)
        Status status = getBestallningsStatus(tjanstekomponent)
        return new ReportPair(status.toString(), tjanstekomponent.hsaId)
    }

    ReportPair getReportData(LogiskadressBestallning bestallning, BestallningsData data) {
        LogiskAdress logiskAdress = data.getLogiskAdress(bestallning)
        Status status = getBestallningsStatus(logiskAdress)
        return new ReportPair(status.toString(), logiskAdress.hsaId)
    }

    ReportPair getReportData(AnropsbehorighetBestallning bestallning, BestallningsData data) {
        Anropsbehorighet anropsbehorighet = data.getAnropsbehorighet(bestallning)
        Status status = getAnropsbehorighetBestallningsStatus(anropsbehorighet, data.bestallning.genomforandeTidpunkt)

        if (anropsbehorighet == null) {
            return new ReportPair(status.toString(), bestallning.toString())
        } else {
            return new ReportPair(status.toString(), transformToString(anropsbehorighet))
        }

    }

    List<ReportPair> getReportData(VagvalBestallning bestallning, BestallningsData data) {
        BestallningsData.VagvalPair vvPair = data.getVagval(bestallning)
        VagvalStatus status = getVagvalBestallningsStatus(vvPair, data.bestallning.genomforandeTidpunkt)
        List<ReportPair> list = new ArrayList<ReportPair>()

        if (vvPair == null) {
            list.add(new ReportPair(status.oldVagvalStatus.toString(), bestallning.toString()))
            return list
        }

        if (status.oldVagvalStatus != null && vvPair.oldVagval != null) {
            list.add(new ReportPair(status.oldVagvalStatus.toString(), transformToString(vvPair.oldVagval)))
        }
        if (status.newVagvalStatus != null && vvPair.newVagval != null) {
            list.add(new ReportPair(status.newVagvalStatus.toString(), transformToString(vvPair.newVagval)))
        }
        return list
    }

    private String transformToString(Vagval element) {
        return element.logiskAdress.hsaId + " - " +
                element.tjanstekontrakt.namnrymd + " - " +
                element.anropsAdress.tjanstekomponent.hsaId + " - " +
                element.anropsAdress.rivTaProfil.namn + " - " +
                element.anropsAdress.adress + " " +
                "(" + element.fromTidpunkt + " - " + element.tomTidpunkt + ")"
    }

    private String transformToString(Anropsbehorighet element) {
        return element.logiskAdress.hsaId + " - " +
                element.tjanstekonsument.hsaId + " - " +
                element.tjanstekontrakt.namnrymd + "  " +
                "(" + element.fromTidpunkt + " - " + element.tomTidpunkt + ")"
    }

    class ReportPair {
        String status
        String value

        ReportPair(String status, String value) {
            this.status = status
            this.value = value
        }

        String getStatus() {
            return status
        }
    }
}
