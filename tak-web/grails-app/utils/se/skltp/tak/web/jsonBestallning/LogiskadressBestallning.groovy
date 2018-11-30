package se.skltp.tak.web.jsonBestallning;


import se.skltp.tak.core.entity.LogiskAdress;

class LogiskadressBestallning {
    private boolean newObject = false
    private String hsaId
    private String beskrivning

    String getHsaId() {
        return hsaId
    }

    private LogiskAdress logiskAdress

    void setHsaId(String hsaId) {
        this.hsaId = JsonUtils.cleanupString(hsaId)
    }

    String getBeskrivning() {
        return beskrivning
    }

    void setBeskrivning(String beskrivning) {
        this.beskrivning = JsonUtils.cleanupString(beskrivning)
    }

    LogiskAdress getLogiskAdress() {
        return logiskAdress
    }

    void setLogiskAdress(LogiskAdress logiskAdress) {
        if(logiskAdress.id == 0l) newObject = true
        this.logiskAdress = logiskAdress
    }

    boolean isNew(){
        return newObject
    }

    @Override
    String toString() {
        return hsaId
    }
}
