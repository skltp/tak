package se.skltp.tak.web.jsonBestallning;

import se.skltp.tak.core.entity.Tjanstekomponent;

public class TjanstekomponentBestallning {
    private String hsaId;
    private String beskrivning;

    private Tjanstekomponent tjanstekomponent;

    public Tjanstekomponent getTjanstekomponent() {
        return tjanstekomponent;
    }

    public void setTjanstekomponent(Tjanstekomponent tjanstekomponent) {
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
    @Override
    public String toString() {
        return hsaId;
    }

}