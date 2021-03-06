package se.skltp.tak.web.jsonBestallning

class LogiskadressBestallning {
    private String hsaId
    private String beskrivning

    String getHsaId() {
        return hsaId
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

    @Override
    String toString() {
        return hsaId
    }

    @Override
    boolean equals(Object obj) {
        if(!Objects instanceof LogiskadressBestallning) return false
        LogiskadressBestallning la = (LogiskadressBestallning) obj;
        return  hsaId.equals(la.hsaId)
    }
}
