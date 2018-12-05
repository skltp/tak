package se.skltp.tak.web.jsonBestallning;

import se.skltp.tak.core.entity.Tjanstekontrakt;

public class TjanstekontraktBestallning {
    private boolean newObject = false;

    private String namnrymd;
    private String beskrivning;
    private long majorVersion;

    private Tjanstekontrakt tjanstekontrakt;

    public Tjanstekontrakt getTjanstekontrakt() {
        return tjanstekontrakt;
    }

    public void setTjanstekontrakt(Tjanstekontrakt tjanstekontrakt) {
        if(tjanstekontrakt.id == 0l) newObject = true
        this.tjanstekontrakt = tjanstekontrakt;
    }

    public String getNamnrymd() {
        return namnrymd;
    }

    public void setNamnrymd(String namnrymd) {
        this.namnrymd = namnrymd;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    public long getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(long majorVersion) {
        this.majorVersion = majorVersion;
    }

    public boolean isNew(){
        return newObject
    }
}
