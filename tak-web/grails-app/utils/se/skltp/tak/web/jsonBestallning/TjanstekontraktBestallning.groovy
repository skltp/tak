package se.skltp.tak.web.jsonBestallning

import com.fasterxml.jackson.annotation.JsonIgnore
import se.skltp.tak.core.entity.Tjanstekontrakt

class TjanstekontraktBestallning {
    @JsonIgnore
    private transient boolean newObject = false
    private String namnrymd
    private String beskrivning
    private long majorVersion
    @JsonIgnore
    private transient Tjanstekontrakt tjanstekontrakt
    @JsonIgnore
    Tjanstekontrakt getTjanstekontrakt() {
        return tjanstekontrakt
    }

    void setTjanstekontrakt(Tjanstekontrakt tjanstekontrakt) {
        if(tjanstekontrakt.id == 0l) newObject = true
        this.tjanstekontrakt = tjanstekontrakt
    }

    String getNamnrymd() {
        return namnrymd
    }

    void setNamnrymd(String namnrymd) {
        this.namnrymd = JsonUtils.cleanupString(namnrymd)
    }

    String getBeskrivning() {
        return beskrivning
    }

    void setBeskrivning(String beskrivning) {
        this.beskrivning = JsonUtils.cleanupString(beskrivning)
    }

    long getMajorVersion() {
        return majorVersion
    }

    void setMajorVersion(long majorVersion) {
        this.majorVersion = majorVersion
    }

    @JsonIgnore
    boolean isNew(){
        return newObject
    }

    @Override
    public String toString() {
        return namnrymd;
    }
}
