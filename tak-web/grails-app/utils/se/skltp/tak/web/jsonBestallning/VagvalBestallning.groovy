package se.skltp.tak.web.jsonBestallning

import com.fasterxml.jackson.annotation.JsonIgnore

import se.skltp.tak.core.entity.Vagval

public class VagvalBestallning {
    private String adress;
    private String logiskAdress;
    private String tjanstekontrakt;
    private String rivtaprofil;
    private String tjanstekomponent;
    @JsonIgnore
    private transient Vagval newVagval;
    @JsonIgnore
    private transient List<Vagval> oldVagval;
    @JsonIgnore
    private transient List<Vagval> vagvalForDelete;

    @JsonIgnore
    List<Vagval> getOldVagval() {
        return oldVagval
    }

    void setOldVagval(List<Vagval> oldVagval) {
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
        return logiskAdress + " - " + tjanstekontrakt + " - " + rivtaprofil + " - " + tjanstekomponent;
    }

}
