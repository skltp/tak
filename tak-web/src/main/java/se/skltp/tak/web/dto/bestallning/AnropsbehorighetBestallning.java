package se.skltp.tak.web.dto.bestallning;

import java.util.Objects;

public class AnropsbehorighetBestallning {
    private String logiskAdress;
    private String tjanstekontrakt;
    private String tjanstekonsument;

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
        return String.format("Logisk adress: %s, Tjänstekonsument: %s, Tjänstekontrakt: %s",
                logiskAdress,
                tjanstekonsument,
                tjanstekontrakt);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AnropsbehorighetBestallning)) return false;
        AnropsbehorighetBestallning ab = (AnropsbehorighetBestallning) obj;
        return  tjanstekonsument.equalsIgnoreCase(ab.tjanstekonsument) &&
                logiskAdress.equalsIgnoreCase(ab.logiskAdress) &&
                tjanstekontrakt.equalsIgnoreCase(ab.tjanstekontrakt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logiskAdress, tjanstekonsument, tjanstekontrakt);
    }

    public boolean hasRequiredFields()
    {
        return (logiskAdress != null) &&
                (tjanstekontrakt != null) &&
                (tjanstekonsument != null);
    }
}
