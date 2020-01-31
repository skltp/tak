package se.skltp.tak.web.jsonBestallning

class TjanstekomponentBestallning {
    private String hsaId
    private String beskrivning

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


    @Override
    public String toString() {
        return hsaId
    }

    @Override
    boolean equals(Object obj) {
        if(!Objects instanceof TjanstekomponentBestallning) return false
        TjanstekomponentBestallning tb = (TjanstekomponentBestallning) obj;
        return  hsaId.equals(tb.hsaId)
    }
}
