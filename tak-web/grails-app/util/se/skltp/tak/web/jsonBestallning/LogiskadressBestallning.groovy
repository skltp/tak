package se.skltp.tak.web.jsonBestallning;


import se.skltp.tak.core.entity.LogiskAdress;

public class LogiskadressBestallning {
    private String hsaId;
    private String beskrivning;

    public String getHsaId() {
        return hsaId;
    }

    private LogiskAdress logiskAdress;

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

    public void setLogiskAdress(LogiskAdress logiskAdress) {
        this.logiskAdress = logiskAdress;
    }
}
