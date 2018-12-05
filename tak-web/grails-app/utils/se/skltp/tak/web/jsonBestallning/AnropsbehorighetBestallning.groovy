package se.skltp.tak.web.jsonBestallning

import com.fasterxml.jackson.annotation.JsonIgnore
import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt

class AnropsbehorighetBestallning {
    private String logiskAdress;
    private String tjanstekontrakt;
    private String tjanstekonsument;

    @JsonIgnore
    private LogiskAdress logiskAdressObject

    @JsonIgnore
    private Tjanstekontrakt tjanstekontraktObject

    @JsonIgnore
    private Tjanstekomponent tjanstekonsumentObject

    @JsonIgnore
    private Anropsbehorighet newAnropsbehorighet;

    @JsonIgnore
    private Anropsbehorighet oldAnropsbehorighet;

    @JsonIgnore
    private List<Anropsbehorighet> aropsbehorigheterForDelete;


    @JsonIgnore
    Anropsbehorighet getOldAnropsbehorighet() {
        return oldAnropsbehorighet
    }

    void setOldAnropsbehorighet(Anropsbehorighet oldAnropsbehorighet) {
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

    @JsonIgnore
    LogiskAdress getLogiskAdressObject() {
        return logiskAdressObject
    }

    void setLogiskAdressObject(LogiskAdress logiskAdressObject) {
        this.logiskAdressObject = logiskAdressObject
    }

    @JsonIgnore
    Tjanstekontrakt getTjanstekontraktObject() {
        return tjanstekontraktObject
    }

    void setTjanstekontraktObject(Tjanstekontrakt tjanstekontraktObject) {
        this.tjanstekontraktObject = tjanstekontraktObject
    }

    @JsonIgnore
    Tjanstekomponent getTjanstekonsumentObject() {
        return tjanstekonsumentObject
    }

    void setTjanstekonsumentObject(Tjanstekomponent tjanstekonsumentObject) {
        this.tjanstekonsumentObject = tjanstekonsumentObject
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
