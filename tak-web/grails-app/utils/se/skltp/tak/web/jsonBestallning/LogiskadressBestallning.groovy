package se.skltp.tak.web.jsonBestallning

import com.fasterxml.jackson.annotation.JsonIgnore
import se.skltp.tak.core.entity.LogiskAdress;

class LogiskadressBestallning {
    @JsonIgnore
    private boolean newObject = false
    private String hsaId
    private String beskrivning

    String getHsaId() {
        return hsaId
    }
    @JsonIgnore
    private transient LogiskAdress logiskAdress


    public void setLogiskAdress(LogiskAdress logiskAdress) {
        if(logiskAdress.id == 0l) newObject = true
        this.logiskAdress = logiskAdress;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = JsonUtils.cleanupString(hsaId)
    }

    String getBeskrivning() {
        return beskrivning
    }

    void setBeskrivning(String beskrivning) {
        this.beskrivning = JsonUtils.cleanupString(beskrivning)
    }

    @JsonIgnore
    LogiskAdress getLogiskAdress() {
        return logiskAdress
    }

    @JsonIgnore
    public boolean isNew(){
        return newObject
    }

    @Override
    String toString() {
        return hsaId
    }
}
