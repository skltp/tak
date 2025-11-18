package se.skltp.tak.web.dto.connection;

import org.springframework.lang.NonNull;
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

    public ConnectionStatus success(Boolean success) {
        this.success = success;
        return this;
    }

    public void setAnalysisResult(AnalysisResultV1 analysisResult) {
        this.analysisResult = analysisResult;
        if (analysisResult != null) {
            checkIfSuccessful(analysisResult);
        }
    }

    private void checkIfSuccessful(@NonNull AnalysisResultV1 analysisResult) {
        var checklist = analysisResult.getConnectionChecklist();
        if (checklist != null) {
            if (url.startsWith("https")) { // mTLS => All in the checklist must pass
                success = Stream.of(
                        checklist.getConnectionOK(),
                        checklist.getNameResolves(),
                        checklist.getHostNameVerifiesOK(),
                        checklist.getServerAppliesMTLS(),
                        checklist.getServerCertificateTrusted(),
                        checklist.getServerTrustsClientCertificate(),
                        checklist.getTlsHandshakeOK()
                ).allMatch(
                        opt -> opt != null && opt.toString().equals(AaaClient.SUCCESS)
                );
            } else { // Non-TLS: Check connection only
                success = Stream.of(
                        checklist.getConnectionOK(),
                        checklist.getNameResolves(),
                        checklist.getHostNameVerifiesOK()
                ).allMatch(
                        opt -> opt != null && opt.toString().equals(AaaClient.SUCCESS)
                );
            }
        }
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
        String normalizedBaseUrl = normalizeBaseUrl(aaaBaseUrl);
        if (normalizedBaseUrl == null) {
            return "#";
        }
        String query = String.format("url=%s&method=HEAD",
                URLEncoder.encode(url, StandardCharsets.UTF_8));
        return normalizedBaseUrl + "?" + query;
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return null;
        }
        return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    }
}
