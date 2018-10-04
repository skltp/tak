package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.annotation.JsonFormat
import se.skltp.tak.core.entity.Vagval;

import java.util.Date;

public class VagvalBestallning {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD hh:mm:ss")
    private Date fromTidpunkt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD hh:mm:ss")
    private Date tomTidpunkt;
    private String adress;
    private String logiskadress;
    private String tjanstekontrakt;
    private String rivtaprofil;
    private String tjanstekomponent;
    private Vagval vagval;

    private VagvalBestallning bestallning;

    public VagvalBestallning getBestallning() {
        return bestallning;
    }

    public void setBestallning(VagvalBestallning bestallning) {
        this.bestallning = bestallning;
    }

    public Date getFromTidpunkt() {
        return fromTidpunkt;
    }

    public void setFromTidpunkt(Date fromTidpunkt) {
        this.fromTidpunkt = fromTidpunkt;
    }

    public Date getTomTidpunkt() {
        return tomTidpunkt;
    }

    public void setTomTidpunkt(Date tomTidpunkt) {
        this.tomTidpunkt = tomTidpunkt;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getLogiskadress() {
        return logiskadress;
    }

    public void setLogiskadress(String logiskadress) {
        this.logiskadress = logiskadress;
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

    public Vagval getVagval() {
        return vagval
    }

    public void setVagval(Vagval vagval) {
        this.vagval = vagval
    }
}
