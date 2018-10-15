package se.skltp.tak.web.jsonBestallning;

import se.skltp.tak.core.entity.Anropsbehorighet

class AnropsbehorighetBestallning {
    private String logiskAdress;
    private String tjanstekontrakt;
    private String tjanstekonsument;

    private Anropsbehorighet anropsbehorighet;

    public Anropsbehorighet getAnropsbehorighet() {
        return anropsbehorighet;
    }

    public void setAnropsbehorighet(Anropsbehorighet anropsbehorighet) {
        this.anropsbehorighet = anropsbehorighet;
    }

    public String getLogiskAdress() {
        return logiskAdress;
    }

    public void setLogiskAdress(String logiskAdress) {
        this.logiskAdress = logiskAdress;
    }

    public String getTjanstekontrakt() {
        return tjanstekontrakt;
    }

    public void setTjanstekontrakt(String tjanstekontrakt) {
        this.tjanstekontrakt = tjanstekontrakt;
    }

    public String getTjanstekonsument() {
        return tjanstekonsument;
    }

    public void setTjanstekonsument(String tjanstekonsument) {
        this.tjanstekonsument = tjanstekonsument;
    }

    @Override
    public String toString() {
        return tjanstekonsument + " - " + tjanstekontrakt + " - " + logiskAdress;
    }
}
