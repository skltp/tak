package se.skltp.tak.web.jsonBestallning;

import se.skltp.tak.core.entity.Tjanstekomponent;

public class TjanstekomponentBestallning {
    private boolean newObject = false;
    private String hsaId;
    private String beskrivning;

    private Tjanstekomponent tjanstekomponent;

    public Tjanstekomponent getTjanstekomponent() {
        return tjanstekomponent;
    }

    public void setTjanstekomponent(Tjanstekomponent tjanstekomponent) {
        if(tjanstekomponent.id == 0l) newObject = true
        this.tjanstekomponent = tjanstekomponent;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }

    public boolean isNew(){
        return newObject
    }

    @Override
    public String toString() {
        return hsaId;
    }

}
