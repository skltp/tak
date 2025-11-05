package se.skltp.tak.web.dto.connection;

import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;
import se.skltp.tak.web.client.AaaClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;

public class ConnectionStatus implements Comparable<ConnectionStatus> {
    private final String aaaUrl;
    private final String hsaId;
    private final String url;
    private Boolean success;
    private AnalysisResultV1 analysisResult;

    public ConnectionStatus(String hsaId, String url, String aaaBaseUrl) {
        this.hsaId = hsaId;
        this.url = url;
        this.aaaUrl = getAaaUrl(aaaBaseUrl);
    }

    public String getHsaId() {
        return hsaId;
    }

    public String getUrl() {
        return url;
    }

    public String getAaaUrl() {
        return aaaUrl;
    }

    public Boolean getSuccess() {
        return success;
    }

    public AnalysisResultV1 getAnalysisResult() {
        return analysisResult;
    }

    public void setAnalysisResult(AnalysisResultV1 analysisResult) {
        if (url.startsWith("https")) { // mTLS => All in the checklist must pass
            analysisResult.getConnectionChecklist().ifPresent(checklist ->
                    success = Stream.of(
                        checklist.getConnectionOK(),
                        checklist.getNameResolves(),
                        checklist.getHostNameVerifiesOK(),
                        checklist.getServerAppliesMTLS(),
                        checklist.getServerCertificateTrusted(),
                        checklist.getServerTrustsClientCertificate(),
                        checklist.getTlsHandshakeOK())
                    .allMatch(
                            opt -> opt.isPresent() && opt.get().toString().equals(AaaClient.SUCCESS))
                    );
        } else { // Non-TLS: Check connection only
            analysisResult.getConnectionChecklist().ifPresent(checklist ->
                    success = Stream.of(
                        checklist.getConnectionOK(),
                        checklist.getNameResolves(),
                        checklist.getHostNameVerifiesOK())
                    .allMatch(
                            opt -> opt.isPresent() && opt.get().toString().equals(AaaClient.SUCCESS))
                    );
        }
        this.analysisResult = analysisResult;
    }

    @Override
    public int compareTo(ConnectionStatus o) {
        int hsaIdComparison = this.getHsaId().compareTo(o.getHsaId());
        if (hsaIdComparison != 0) {
            return hsaIdComparison;
        }
        return this.getUrl().compareTo(o.getUrl());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionStatus that = (ConnectionStatus) o;
        return Objects.equals(getHsaId(), that.getHsaId()) && Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHsaId(), getUrl());
    }

    @Override
    public String toString() {
        return "ConnectionStatus{" +
                "hsaId='" + hsaId + '\'' +
                ", url='" + url + '\'' +
                ", success=" + success +
                ", analysisResult=" + analysisResult +
                '}';
    }

    private String getAaaUrl(String aaaBaseUrl) {
        if (aaaBaseUrl == null || aaaBaseUrl.isBlank()) {
            return "#";
        }
        String query = String.format("url=%s&method=HEAD",
                URLEncoder.encode(url, StandardCharsets.UTF_8));
        return aaaBaseUrl + "?" + query;
    }
}
