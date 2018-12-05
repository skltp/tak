package se.skltp.tak.web.jsonBestallning

import com.fasterxml.jackson.annotation.JsonIgnore;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.core.entity.AnropsAdress
import se.skltp.tak.core.entity.LogiskAdress
import se.skltp.tak.core.entity.RivTaProfil
import se.skltp.tak.core.entity.Tjanstekomponent
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.entity.Vagval;

public class VagvalBestallning {
    private String adress;
    private String logiskAdress;
    private String tjanstekontrakt;
    private String rivtaprofil;
    private String tjanstekomponent;

    @JsonIgnore
    private Vagval newVagval;

    @JsonIgnore
    private Vagval oldVagval;

    @JsonIgnore
    private List<Vagval> vagvalForDelete;

    @JsonIgnore
    private LogiskAdress logiskAdressObject

    @JsonIgnore
    private Tjanstekontrakt tjanstekontraktObject

    @JsonIgnore
    private Tjanstekomponent tjanstekomponentObject

    @JsonIgnore
    private RivTaProfil rivtaprofilObject

    @JsonIgnore
    private AnropsAdress anropsAdressObject

    @JsonIgnore
    AnropsAdress getAnropsAdressObject() {
        return anropsAdressObject
    }

    void setAnropsAdressObject(AnropsAdress anropsAdressObject) {
        this.anropsAdressObject = anropsAdressObject
    }

    void setLogiskAdress(String logiskAdress) {
        this.logiskAdress = logiskAdress
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
    Tjanstekomponent getTjanstekomponentObject() {
        return tjanstekomponentObject
    }

    void setTjanstekomponentObject(Tjanstekomponent tjanstekomponentObject) {
        this.tjanstekomponentObject = tjanstekomponentObject
    }

    @JsonIgnore
    RivTaProfil getRivtaprofilObject() {
        return rivtaprofilObject
    }

    void setRivtaprofilObject(RivTaProfil rivtaprofilObject) {
        this.rivtaprofilObject = rivtaprofilObject
    }

    @JsonIgnore
    Vagval getOldVagval() {
        return oldVagval
    }

    void setOldVagval(Vagval oldVagval) {
        this.oldVagval = oldVagval
    }


    @JsonIgnore
    List<Vagval> getVagvalForDelete() {
        return vagvalForDelete
    }

    void setVagvalForDelete(List<Vagval> vagvalForDelete) {
        this.vagvalForDelete = vagvalForDelete
    }

    @JsonIgnore
    Vagval getNewVagval() {
        return newVagval
    }

    void setNewVagval(Vagval vagval) {
        this.newVagval = vagval
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getLogiskAdress() {
        return logiskAdress;
    }

    public void setLogiskadress(String logiskAdress) {
        this.logiskAdress = logiskAdress;
    }

    public String getTjanstekontrakt() {
        return tjanstekontrakt;
    }

    public void setTjanstekontrakt(String tjanstekontrakt) {
        this.tjanstekontrakt = tjanstekontrakt;
    }

    public String getRivtaprofil() {
        return rivtaprofil;
    }

    public void setRivtaprofil(String rivtaprofil) {
        this.rivtaprofil = rivtaprofil;
    }

    public String getTjanstekomponent() {
        return tjanstekomponent;
    }

    public void setTjanstekomponent(String tjanstekomponent) {
        this.tjanstekomponent = tjanstekomponent;
    }

    @Override
    public String toString() {
        return logiskAdress + " - " + tjanstekontrakt + " - " + adress;
    }
}
