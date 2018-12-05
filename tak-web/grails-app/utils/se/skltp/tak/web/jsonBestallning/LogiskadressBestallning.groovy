package se.skltp.tak.web.jsonBestallning;


import se.skltp.tak.core.entity.LogiskAdress;

public class LogiskadressBestallning {
    private boolean newObject = false;
    private String hsaId;
    private String beskrivning;

    public String getHsaId() {
        return hsaId;
    }

    private LogiskAdress logiskAdress;

    public void setLogiskAdress(LogiskAdress logiskAdress) {
        if(logiskAdress.id == 0l) newObject = true
        this.logiskAdress = logiskAdress;
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

    public LogiskAdress getLogiskAdress() {
        return logiskAdress;
    }

    public boolean isNew(){
        return newObject
    }
}
