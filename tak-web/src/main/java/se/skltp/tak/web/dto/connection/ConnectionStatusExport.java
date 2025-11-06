package se.skltp.tak.web.dto.connection;

public class ConnectionStatusExport {
    private String serviceProducer;
    private String baseAddress;
    private Boolean success;
    private String tlsProtocol;

    public String getServiceProducer() {
        return serviceProducer;
    }

    public void setServiceProducer(String serviceProducer) {
        this.serviceProducer = serviceProducer;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public void setBaseAddress(String baseAddress) {
        this.baseAddress = baseAddress;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getTlsProtocol() {
        return tlsProtocol;
    }

    public void setTlsProtocol(String tlsProtocol) {
        this.tlsProtocol = tlsProtocol;
    }
}
