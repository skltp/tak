package se.skltp.tak.web.dto;

public class PodInfo {

    private final String name;
    private final String ip;
    private final String phase;

    public PodInfo(String name, String ip, String phase) {
        this.name = name;
        this.ip = ip;
        this.phase = phase;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getPhase() {
        return phase;
    }
}