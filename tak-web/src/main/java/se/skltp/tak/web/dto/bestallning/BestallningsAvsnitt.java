package se.skltp.tak.web.dto.bestallning;

import java.util.ArrayList;
import java.util.List;

public class BestallningsAvsnitt {
    private List<TjanstekontraktBestallning> tjanstekontrakt;
    private List<LogiskadressBestallning> logiskadresser;
    private List<TjanstekomponentBestallning> tjanstekomponenter;
    private List<AnropsbehorighetBestallning> anropsbehorigheter;
    private List<VagvalBestallning> vagval;

    public List<TjanstekontraktBestallning> getTjanstekontrakt() {
        return tjanstekontrakt == null ? new ArrayList<>() : tjanstekontrakt;
    }

    public void setTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontrakt) {
        this.tjanstekontrakt = tjanstekontrakt;
    }

    public List<LogiskadressBestallning> getLogiskadresser() {
        return logiskadresser == null ? new ArrayList<>() : logiskadresser;
    }

    public void setLogiskadresser(List<LogiskadressBestallning> logiskadresser) {
        this.logiskadresser = logiskadresser;
    }

    public List<TjanstekomponentBestallning> getTjanstekomponenter() {
        return tjanstekomponenter == null ? new ArrayList<>() : tjanstekomponenter;
    }

    public void setTjanstekomponenter(List<TjanstekomponentBestallning> tjanstekomponenter) {
        this.tjanstekomponenter = tjanstekomponenter;
    }

    public List<AnropsbehorighetBestallning> getAnropsbehorigheter() {
        return anropsbehorigheter == null ? new ArrayList<>() : anropsbehorigheter;
    }

    public void setAnropsbehorigheter(List<AnropsbehorighetBestallning> anropsbehorigheter) {
        this.anropsbehorigheter = anropsbehorigheter;
    }

    public List<VagvalBestallning> getVagval() {
        return vagval == null ? new ArrayList<>() : vagval;
    }

    public void setVagval(List<VagvalBestallning> vagval) {
        this.vagval = vagval;
    }
}
