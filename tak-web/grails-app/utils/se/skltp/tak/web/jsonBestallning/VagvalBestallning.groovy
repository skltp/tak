package se.skltp.tak.web.jsonBestallning

class VagvalBestallning {
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
        return  logiskAdress + " - " + tjanstekontrakt + " - " + tjanstekomponent + " - " + rivtaprofil + " - " + adress
    }

    @Override
    boolean equals(Object obj) {
        if(!Objects instanceof VagvalBestallning) return false
        VagvalBestallning vv = (VagvalBestallning) obj;
        return  logiskAdress.equals(vv.logiskAdress) &&
                tjanstekontrakt.equals(vv.tjanstekontrakt)

    }
}
