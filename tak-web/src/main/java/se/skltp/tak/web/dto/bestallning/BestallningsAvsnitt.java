package se.skltp.tak.web.dto.bestallning;

import java.util.ArrayList;
import java.util.List;

public class BestallningsAvsnitt {
    private List<TjanstekontraktBestallning> tjanstekontrakt = new ArrayList<>();
    private List<LogiskadressBestallning> logiskadresser = new ArrayList<>();
    private List<TjanstekomponentBestallning> tjanstekomponenter = new ArrayList<>();
    private List<AnropsbehorighetBestallning> anropsbehorigheter = new ArrayList<>();
    private List<VagvalBestallning> vagval = new ArrayList<>();

    public List<TjanstekontraktBestallning> getTjanstekontrakt() {
        return tjanstekontrakt;
    }

    public void setTjanstekontrakt(List<TjanstekontraktBestallning> tjanstekontrakt) {
        this.tjanstekontrakt = tjanstekontrakt;
    }

    public List<LogiskadressBestallning> getLogiskadresser() {
        return logiskadresser;
    }

    public void setLogiskadresser(List<LogiskadressBestallning> logiskadresser) {
        this.logiskadresser = logiskadresser;
    }

    public List<TjanstekomponentBestallning> getTjanstekomponenter() {
        return tjanstekomponenter;
    }

    public void setTjanstekomponenter(List<TjanstekomponentBestallning> tjanstekomponenter) {
        this.tjanstekomponenter = tjanstekomponenter;
    }

    public List<AnropsbehorighetBestallning> getAnropsbehorigheter() {
        return anropsbehorigheter;
    }

    public void setAnropsbehorigheter(List<AnropsbehorighetBestallning> anropsbehorigheter) {
        this.anropsbehorigheter = anropsbehorigheter;
    }

    public List<VagvalBestallning> getVagval() {
        return vagval;
    }

    public void setVagval(List<VagvalBestallning> vagval) {
        this.vagval = vagval;
    }
}
