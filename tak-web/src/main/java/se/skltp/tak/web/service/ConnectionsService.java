package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.aaa.client.model.AnalysisRequestV1;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;
import se.skltp.tak.web.client.AaaClient;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Service
public class ConnectionsService {

    private final Optional<AaaClient> aaaClient;

    private static final Pattern PROTO_HOST_PORT = Pattern.compile("^(https?://[^/]+).*", Pattern.CASE_INSENSITIVE);

    private static final Logger log = LoggerFactory.getLogger(ConnectionsService.class);

    private final AnropsAdressRepository anropsAdressRepository;

    public ConnectionsService(Optional<AaaClient> aaaClient, AnropsAdressRepository anropsAdressRepository) {
        this.aaaClient = aaaClient;
        this.anropsAdressRepository = anropsAdressRepository;
    }

    public PagedEntityList<ConnectionStatus> getActive(Integer offset, Integer max) {
        log.info("getActive {} {}", offset, max);
        List<ConnectionStatus> contents = anropsAdressRepository.findActive().stream()
                .map(ConnectionsService::toConnectionStatus)
                .sorted()
                .distinct()
                .skip(offset)
                .limit(max)
                .toList();
        applyAnalysisResult(contents);
        long total = anropsAdressRepository.count();
        return new PagedEntityList<>(contents, (int) total, offset, max);
    }

    private void applyAnalysisResult(List<ConnectionStatus> connectionStatus) {
        List<AnalysisRequestV1> requests = connectionStatus.stream()
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
                resultMap.put(result.getUrl().orElse("?"), result);
            }
            for (var status: connectionStatus) {
                status.setAnalysisResult(resultMap.getOrDefault(status.getUrl(), null));
            }
        });
    }

    private static ConnectionStatus toConnectionStatus(AnropsAdress anropsAdress) {
        String address;
        Matcher matcher = PROTO_HOST_PORT.matcher(anropsAdress.getAdress());
        if (matcher.matches()) {
            address = matcher.group(1);
        } else {
            address = anropsAdress.getAdress();
            log.warn("Couldn't match '{}' with '{}' ({})", address, PROTO_HOST_PORT, matcher);
        }
        return new ConnectionStatus(anropsAdress.getTjanstekomponent().getHsaId(), address);
    }
}
