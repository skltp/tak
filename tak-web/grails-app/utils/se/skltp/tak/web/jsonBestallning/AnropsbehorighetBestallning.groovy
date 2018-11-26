package se.skltp.tak.web.jsonBestallning;

import se.skltp.tak.core.entity.Anropsbehorighet

class AnropsbehorighetBestallning {
    private String logiskAdress;
    private String tjanstekontrakt;
    private String tjanstekonsument;

    private List<Anropsbehorighet> aropsbehorigheterForDelete;

    private Anropsbehorighet anropsbehorighet;

    Anropsbehorighet getAnropsbehorighet() {
        return anropsbehorighet
    }

    void setAnropsbehorighet(Anropsbehorighet anropsbehorighet) {
        this.anropsbehorighet = anropsbehorighet
    }

    List<Anropsbehorighet> getAropsbehorigheterForDelete() {
        return aropsbehorigheterForDelete
    }

    void setAropsbehorigheterForDelete(List<Anropsbehorighet> aropsbehorigheterForDelete) {
        this.aropsbehorigheterForDelete = aropsbehorigheterForDelete
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
