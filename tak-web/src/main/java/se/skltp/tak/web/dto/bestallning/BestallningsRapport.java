/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.dto.bestallning;

import se.skltp.tak.core.entity.*;

import java.time.LocalDate;
import java.util.*;

public class BestallningsRapport {

    private static final String FORMAT_KEY_VALUE = "%s: %s%n";

    private final Map<String, List<ReportPair>> inkludera;
    private final Map<String, String> rapportHuvud;
    private final Map<String, List<ReportPair>> exkludera;

    public BestallningsRapport(BestallningsData data) {
        this.rapportHuvud = buildRapportHuvud(data.getBestallning());
        this.inkludera = buildAvsnitt(data.getBestallning().getInkludera(), data);
        this.exkludera = buildAvsnitt(data.getBestallning().getExkludera(), data);
    }

    public Map<String, String> getRapportHuvud() {
        return rapportHuvud;
    }

    public Map<String, List<ReportPair>> getInkludera() {
        return inkludera;
    }

    public Map<String, List<ReportPair>> getExkludera() {
        return exkludera;
    }

    public String toString() {
        StringBuilder report = new StringBuilder();

        for (Map.Entry<String, String> d: getRapportHuvud().entrySet()) {
            report.append(String.format(FORMAT_KEY_VALUE, d.getKey(), d.getValue()));
        }

        report.append("\nInkludera:\n");
        for (Map.Entry<String, List<ReportPair>> i: getInkludera().entrySet()) {
            report.append(i.getKey()).append(":\n");
            for (ReportPair p : i.getValue()) {
                report.append(String.format(FORMAT_KEY_VALUE, p.getStatus(), p.getValue()));
            }
        }

        report.append("\nExkludera:\n");
        for (Map.Entry<String, List<ReportPair>> e : getExkludera().entrySet()) {
            report.append(e.getKey()).append(":\n");
            for (ReportPair p : e.getValue()) {
                report.append(String.format(FORMAT_KEY_VALUE, p.getStatus(), p.getValue()));
            }
        }

        return report.toString();
    }

    private Map<String, String> buildRapportHuvud(JsonBestallning bestallning) {
        Map<String, String> metadata = new LinkedHashMap<>(); // Preserves order
        metadata.put("Plattform", bestallning.getPlattform());
        metadata.put("Format Version", Float.toString(bestallning.getFormatVersion()));
        metadata.put("Version", Integer.toString(bestallning.getVersion()));
        metadata.put("Beställningstidpunkt",  bestallning.getBestallningsTidpunkt().toString());
        metadata.put("Genomförandetidpunkt",  bestallning.getGenomforandeTidpunkt().toString());
        metadata.put("Utförare", bestallning.getUtforare());
        metadata.put("Kommentar", bestallning.getKommentar());

        return metadata;
    }

    private Map<String, List<ReportPair>> buildAvsnitt(BestallningsAvsnitt bestallningsAvsnitt, BestallningsData data) {
        Map<String, List<ReportPair>> avsnitt = new LinkedHashMap<>();

        addIfNotEmpty(avsnitt, "Logiska adresser", bestallningsAvsnitt.getLogiskadresser().stream()
                .map(it -> getReportData(it, data)).toList());

        addIfNotEmpty(avsnitt, "Tjänstekomponenter", bestallningsAvsnitt.getTjanstekomponenter().stream()
                .map(it -> getReportData(it, data)).toList());

        addIfNotEmpty(avsnitt, "Tjänstekontrakt", bestallningsAvsnitt.getTjanstekontrakt().stream()
                .map(it -> getReportData(it, data)).toList());

        addIfNotEmpty(avsnitt, "Anropsbehörigheter", bestallningsAvsnitt.getAnropsbehorigheter().stream()
                .map(it -> getReportData(it, data)).toList());


        List<ReportPair> vagval = bestallningsAvsnitt.getVagval().stream()
                .flatMap(it -> getReportData(it, data).stream())
                .toList();
        addIfNotEmpty(avsnitt, "Vägval", vagval);

        return avsnitt;
    }

    private void addIfNotEmpty(Map<String, List<ReportPair>> avsnitt, String key, List<ReportPair> list) {
        if (!list.isEmpty()) {
            avsnitt.put(key, list);
        }
    }

    private Status getBestallningsStatus(AbstractVersionInfo entity) {
        if (entity == null) return Status.NOT_EXISTS;
        if (entity.getClass().equals(LogiskAdress.class) && ((LogiskAdress)entity).getId() == 0L) return Status.NEW;
        if (entity.getClass().equals(Tjanstekontrakt.class) && ((Tjanstekontrakt)entity).getId() == 0L) return Status.NEW;
        if (entity.getClass().equals(Tjanstekomponent.class) && ((Tjanstekomponent)entity).getId() == 0L) return Status.NEW;
        if (Boolean.TRUE.equals(entity.getDeleted())) return Status.DELETED;
        else return Status.UPDATED;
    }

    private Status getAnropsbehorighetBestallningsStatus(Anropsbehorighet anropsbehorighet, LocalDate genomforandeTidpunkt) {
        if (anropsbehorighet == null) return Status.NOT_EXISTS;
        if (anropsbehorighet.getId() == 0L) return Status.NEW;
        if (Boolean.TRUE.equals(anropsbehorighet.getDeleted())) return Status.DELETED;
        if (genomforandeTidpunkt.isAfter(anropsbehorighet.getTomTidpunkt().toLocalDate())) return Status.DEACTIVATED;
        else return Status.UPDATED;
    }

    private VagvalStatus getVagvalBestallningsStatus(BestallningsData.VagvalPair vagval, LocalDate genomforandeTidpunkt) {
        VagvalStatus vagvalStatus = new VagvalStatus();

        if (vagval == null) {
            vagvalStatus.oldVagvalStatus = Status.NOT_EXISTS;
            return vagvalStatus;
        }

        if (vagval.getNewVagval() != null) {
            vagvalStatus.newVagvalStatus = Status.NEW;
        }

        if (vagval.getOldVagval() != null) {
            if (Boolean.TRUE.equals(vagval.getOldVagval().getDeleted())) vagvalStatus.oldVagvalStatus = Status.DELETED;
            else if (genomforandeTidpunkt.isAfter(vagval.getOldVagval().getTomTidpunkt().toLocalDate())) vagvalStatus.oldVagvalStatus = Status.DEACTIVATED;
            else vagvalStatus.oldVagvalStatus = Status.UPDATED;
        }

        return vagvalStatus;
    }

    ReportPair getReportData(TjanstekontraktBestallning bestallning, BestallningsData data) {
        Tjanstekontrakt tjanstekontrakt = data.getTjanstekontrakt(bestallning);
        Status status = getBestallningsStatus(tjanstekontrakt);
        if(tjanstekontrakt == null){
            return new ReportPair(status.toString(), bestallning.toString());
        }else{
            return new ReportPair(status.toString(), tjanstekontrakt.getNamnrymd());
        }

    }

    ReportPair getReportData(TjanstekomponentBestallning bestallning, BestallningsData data) {
        Tjanstekomponent tjanstekomponent = data.getTjanstekomponent(bestallning);
        Status status = getBestallningsStatus(tjanstekomponent);
        if(tjanstekomponent == null){
            return new ReportPair(status.toString(), bestallning.toString());
        } else {
            return new ReportPair(status.toString(), tjanstekomponent.getHsaId());
        }
    }

    ReportPair getReportData(LogiskadressBestallning bestallning, BestallningsData data) {
        LogiskAdress logiskAdress = data.getLogiskAdress(bestallning);
        Status status = getBestallningsStatus(logiskAdress);
        if(logiskAdress == null){
            return new ReportPair(status.toString(), bestallning.toString());
        } else{
            return new ReportPair(status.toString(), logiskAdress.getHsaId());
        }


    }

    ReportPair getReportData(AnropsbehorighetBestallning bestallning, BestallningsData data) {
        Anropsbehorighet anropsbehorighet = data.getAnropsbehorighet(bestallning);
        Status status = getAnropsbehorighetBestallningsStatus(anropsbehorighet, data.getBestallning().getGenomforandeTidpunkt());

        if (anropsbehorighet == null) {
            return new ReportPair(status.toString(), bestallning.toString());
        } else {
            return new ReportPair(status.toString(), transformToString(anropsbehorighet));
        }

    }

    List<ReportPair> getReportData(VagvalBestallning bestallning, BestallningsData data) {
        BestallningsData.VagvalPair vvPair = data.getVagval(bestallning);
        VagvalStatus status = getVagvalBestallningsStatus(vvPair, data.getBestallning().getGenomforandeTidpunkt());
        List<ReportPair> list = new ArrayList<ReportPair>();

        if (vvPair == null) {
            list.add(new ReportPair(status.oldVagvalStatus.toString(), bestallning.toString()));
            return list;
        }

        if (status.oldVagvalStatus != null && vvPair.getOldVagval() != null) {
            list.add(new ReportPair(status.oldVagvalStatus.toString(), transformToString(vvPair.getOldVagval())));
        }
        if (status.newVagvalStatus != null && vvPair.getNewVagval() != null) {
            list.add(new ReportPair(status.newVagvalStatus.toString(), transformToString(vvPair.getNewVagval())));
        }
        return list;
    }

    private String transformToString(Vagval element) {
        return element.getLogiskAdress().getHsaId() + " - " +
                element.getTjanstekontrakt().getNamnrymd() + " - " +
                element.getAnropsAdress().getTjanstekomponent().getHsaId() + " - " +
                element.getAnropsAdress().getRivTaProfil().getNamn() + " - " +
                element.getAnropsAdress().getAdress() + " " +
                "(" + element.getFromTidpunkt() + " - " + element.getTomTidpunkt() + ")";
    }

    private String transformToString(Anropsbehorighet element) {
        return element.getLogiskAdress().getHsaId() + " - " +
                element.getTjanstekonsument().getHsaId() + " - " +
                element.getTjanstekontrakt().getNamnrymd() + "  " +
                "(" + element.getFromTidpunkt() + " - " + element.getTomTidpunkt() + ")";
    }

    public enum Status {
        NEW("Nyskapad"),
        UPDATED("Uppdaterad"),
        DELETED("Borttagen"),
        DEACTIVATED("Deaktiverad"),
        NOT_EXISTS("Existerar ej");

        private Status(String description) {
            this.description = description;
        }

        private String description;

        @Override
        public String toString() {
            return description;
        }
    }

    public class VagvalStatus {
        private Status newVagvalStatus;
        private Status oldVagvalStatus;

        Status getNewVagvalStatus() {
            return newVagvalStatus;
        }

        Status getOldVagvalStatus() {
            return oldVagvalStatus;
        }

        void setNewVagvalStatus(Status newVagvalStatus) {
            this.newVagvalStatus = newVagvalStatus;
        }

        void setOldVagvalStatus(Status oldVagvalStatus) {
            this.oldVagvalStatus = oldVagvalStatus;
        }
    }

    public class ReportPair {
        String status;
        String value;
        boolean warning;

        ReportPair(String status, String value) {
            this.status = status;
            this.value = value;

            // Visas med annat utseende för att indikera problem
            warning = (Objects.equals(status, Status.NOT_EXISTS.toString()));
        }

        public String getStatus() {
            return status;
        }
        public String getValue() {
            return value;
        }
        public boolean getWarning() {
            return warning;
        }
    }
}
