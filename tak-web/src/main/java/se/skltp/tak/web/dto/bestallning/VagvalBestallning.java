package se.skltp.tak.web.dto.bestallning;

public class VagvalBestallning {
    private String adress;
    private String logiskAdress;
    private String tjanstekontrakt;
    private String rivtaprofil;
    private String tjanstekomponent;

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
        return String.format("Logisk adress: %s, Tjänstekontrakt: %s, Tjänstekomponent: %s, RivTaProfil: %s, Adress: %s",
                logiskAdress,
                tjanstekontrakt,
                tjanstekomponent,
                rivtaprofil,
                adress);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof VagvalBestallning)) return false;
        VagvalBestallning vv = (VagvalBestallning) obj;
        return  logiskAdress.equals(vv.logiskAdress) &&
                tjanstekontrakt.equals(vv.tjanstekontrakt);

    }

    public boolean hasRequiredFieldsForInclude()
    {
        return (adress != null) &&
                (rivtaprofil != null) &&
                (tjanstekomponent != null) &&
                (logiskAdress != null) &&
                (tjanstekontrakt != null);
    }

    public boolean hasRequiredFieldsForExclude()
    {
        return (rivtaprofil != null) &&
                (tjanstekomponent != null) &&
                (logiskAdress != null) &&
                (tjanstekontrakt != null);
    }
}
