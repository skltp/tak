package se.skltp.tak.web.jsonBestallning;

import com.fasterxml.jackson.annotation.JsonFormat;
import se.skltp.tak.core.entity.Anropsbehorighet;

import java.util.Date;


public class AnropsbehorighetBestallning {
    private String logiskAdress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD hh:mm:ss")
    private Date fromTidpunkt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD hh:mm:ss")
    private Date tomTidpunkt;
    private String tjanstekontrakt;
    private String tjanstekonsument;

    private Anropsbehorighet anropsbehorighet;

    public Anropsbehorighet getAnropsbehorighet() {
        return anropsbehorighet;
    }

    public void setAnropsbehorighet(Anropsbehorighet anropsbehorighet) {
        this.anropsbehorighet = anropsbehorighet;
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
