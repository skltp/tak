package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.aaa.client.model.AnalysisRequestV1;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;
import se.skltp.tak.web.client.AaaClient;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Service
public class ConnectionsService {

    public static final int MAX_PORT = 65535;
    @Value("${aaa.url:}")
    private String aaaUrl;

    private final Optional<AaaClient> aaaClient;

    private static final Pattern PROTO_HOST_PORT = Pattern.compile("^(https?://[^/]+).*");
    private static final Pattern ENDS_WITH_PORT = Pattern.compile(".*:\\d+$");

    private static final Logger log = LoggerFactory.getLogger(ConnectionsService.class);

    private final AnropsAdressRepository anropsAdressRepository;

    public ConnectionsService(Optional<AaaClient> aaaClient, AnropsAdressRepository anropsAdressRepository) {
        this.aaaClient = aaaClient;
        this.anropsAdressRepository = anropsAdressRepository;
    }

    public boolean isAvailable() {
        return aaaClient.isPresent();
    }

    public PagedEntityList<ConnectionStatus> getActive(Integer offset, Integer max) {
        log.debug("getActive {} {}", offset, max);
        List<ConnectionStatus> all = anropsAdressRepository.findActive().stream()
                .map(this::toConnectionStatus)
                .sorted()
                .distinct()
                .toList();
        int total = all.size();
        List<ConnectionStatus> page = all.stream()
                .skip(offset)
                .limit(max)
                .toList();
        applyAnalysisResult(page);
        return new PagedEntityList<>(page, total, offset, max);
    }

    void applyAnalysisResult(List<ConnectionStatus> connectionStatus) {
        List<AnalysisRequestV1> requests = connectionStatus.stream()
                .filter(cs -> cs.getSuccess() == null)
                .map(ConnectionStatus::getUrl)
                .sorted()
                .distinct()
                .map(url -> new AnalysisRequestV1(url).method("HEAD"))
                .toList();
        aaaClient.ifPresent(client -> {
            List<AnalysisResultV1> results = client.analyze(requests);
            log.info("{} => {}", requests, results);
            Map<String, AnalysisResultV1> resultMap = new HashMap<>();
            for (var result: results) {
                resultMap.put(Optional.ofNullable(result.getUrl()).orElse("?"), result);
            }
            for (var status: connectionStatus) {
                status.setAnalysisResult(resultMap.getOrDefault(status.getUrl(), null));
            }
        });
    }

    ConnectionStatus toConnectionStatus(AnropsAdress anropsAdress) {
        Boolean success = null;
        String lowerAddress = anropsAdress.getAdress().toLowerCase();
        String address;
        Matcher matcher = PROTO_HOST_PORT.matcher(lowerAddress);
        if (matcher.matches()) {
            address = normalize(matcher.group(1));
            if (!checkUrl(address)) {
                success = false;
            }
        } else {
            address = lowerAddress;
            success = false;
            log.warn("Couldn't match '{}' with '{}' ({})", address, PROTO_HOST_PORT, matcher);
        }
        return new ConnectionStatus(anropsAdress.getTjanstekomponent().getHsaId(), address, aaaUrl)
                .success(success);
    }

    public String getEntityName() {
        return "Anslutningar";
    }

    private String normalize(String address) {
        Matcher matcher = ENDS_WITH_PORT.matcher(address);
        if (!matcher.matches()) {
            if (address.startsWith("https")) {
                return address + ":443";
            } else {
                return address + ":80";
            }
        }
        return address;
    }

    boolean checkUrl(String urlString) {
        try {
            URI uri = new URI(urlString);
            if (uri.getHost() == null) {
                log.info("Domännamn felaktigt: {}", urlString);
                return false;
            }
            if (uri.getPort() > MAX_PORT) {
                log.info("Port felaktig: {}", urlString);
                return false;
            }
            if (uri.getUserInfo() != null) {
                log.info("Användarinfo är inte tillåten: {}", urlString);
                return false;
            }
        } catch (URISyntaxException ex) {
            log.info("{}: {}", ex.getReason(), ex.getInput());
            return false;
        }
        return true;
    }
}
