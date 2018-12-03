package se.skltp.tak.web.jsonBestallning

import com.fasterxml.jackson.annotation.JsonIgnore
import se.skltp.tak.core.entity.Anropsbehorighet

class AnropsbehorighetBestallning {
    private String logiskAdress;
    private String tjanstekontrakt;
    private String tjanstekonsument;
    @JsonIgnore
    private transient List<Anropsbehorighet> aropsbehorigheterForDelete;
    @JsonIgnore
    private transient Anropsbehorighet newAnropsbehorighet;
    @JsonIgnore
    private transient List<Anropsbehorighet> oldAnropsbehorighet;
    @JsonIgnore
    List<Anropsbehorighet> getOldAnropsbehorighet() {
        return oldAnropsbehorighet
    }

    void setOldAnropsbehorighet(List<Anropsbehorighet> oldAnropsbehorighet) {
        this.oldAnropsbehorighet = oldAnropsbehorighet
    }
    @JsonIgnore
    Anropsbehorighet getNewAnropsbehorighet() {
        return newAnropsbehorighet
    }

    void setNewAnropsbehorighet(Anropsbehorighet anropsbehorighet) {
        this.newAnropsbehorighet = anropsbehorighet
    }
    @JsonIgnore
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
        return logiskAdress + " - " + tjanstekonsument + " - " + tjanstekontrakt + " - " + logiskAdress;
    }
}
