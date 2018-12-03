package se.skltp.tak.web.jsonBestallning

import com.fasterxml.jackson.annotation.JsonIgnore
import se.skltp.tak.core.entity.Tjanstekomponent

public class TjanstekomponentBestallning {
    @JsonIgnore
    private transient boolean newObject = false
    private String hsaId
    private String beskrivning
    @JsonIgnore
    private transient Tjanstekomponent tjanstekomponent

    @JsonIgnore
    public Tjanstekomponent getTjanstekomponent() {
        return tjanstekomponent
    }

    public void setTjanstekomponent(Tjanstekomponent tjanstekomponent) {
        if(tjanstekomponent.id == 0l) newObject = true
        this.tjanstekomponent = tjanstekomponent
    }

    public String getHsaId() {
        return hsaId
    }

    public void setHsaId(String hsaId) {
        this.hsaId = JsonUtils.cleanupString(hsaId)
    }

    public String getBeskrivning() {
        return beskrivning
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = JsonUtils.cleanupString(beskrivning)
    }

    @JsonIgnore
    public boolean isNew(){
        return newObject
    }

    @Override
    public String toString() {
        return hsaId
    }

}
