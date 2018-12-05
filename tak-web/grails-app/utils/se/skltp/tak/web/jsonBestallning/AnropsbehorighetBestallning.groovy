package se.skltp.tak.web.jsonBestallning;

import se.skltp.tak.core.entity.Anropsbehorighet
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt

class AnropsbehorighetBestallning {
    private String logiskAdress;
    private String tjanstekontrakt;
    private String tjanstekonsument;

    private LogiskAdress logiskAdressObject
    private Tjanstekontrakt tjanstekontraktObject
    private Tjanstekomponent tjanstekonsumentObject

    private Anropsbehorighet newAnropsbehorighet;
    private Anropsbehorighet oldAnropsbehorighet;

    private List<Anropsbehorighet> aropsbehorigheterForDelete;


    LogiskAdress getLogiskAdressObject() {
        return logiskAdressObject
    }

    void setLogiskAdressObject(LogiskAdress logiskAdressObject) {
        this.logiskAdressObject = logiskAdressObject
    }

    Tjanstekontrakt getTjanstekontraktObject() {
        return tjanstekontraktObject
    }

    void setTjanstekontraktObject(Tjanstekontrakt tjanstekontraktObject) {
        this.tjanstekontraktObject = tjanstekontraktObject
    }

    Tjanstekomponent getTjanstekonsumentObject() {
        return tjanstekonsumentObject
    }

    void setTjanstekonsumentObject(Tjanstekomponent tjanstekonsumentObject) {
        this.tjanstekonsumentObject = tjanstekonsumentObject
    }

    Anropsbehorighet getOldAnropsbehorighet() {
        return oldAnropsbehorighet
    }

    void setOldAnropsbehorighet(Anropsbehorighet oldAnropsbehorighet) {
        this.oldAnropsbehorighet = oldAnropsbehorighet
    }

    Anropsbehorighet getNewAnropsbehorighet() {
        return newAnropsbehorighet
    }

    void setNewAnropsbehorighet(Anropsbehorighet anropsbehorighet) {
        this.newAnropsbehorighet = anropsbehorighet
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
}
