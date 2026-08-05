/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;
import se.skltp.tak.web.client.AaaClient;
import se.skltp.tak.web.configuration.AaaConfig;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Service
public class ConnectionsService {

    public static final int MAX_PORT = 65535;

    /** HTTP method used when asking AAA to analyze a connection. */
    static final String ANALYZE_METHOD = "HEAD";

    /** Upper bound for how much of an error message that is kept for presentation. */
    static final int MAX_ERROR_MESSAGE_LENGTH = 300;

    private final Optional<AaaClient> aaaClient;

    private final AaaConfig aaaConfig;

    private static final Pattern PROTO_HOST_PORT = Pattern.compile("^(https?://[^/]++).*");
    private static final Pattern ENDS_WITH_PORT = Pattern.compile(".*:\\d+$");

    private static final Logger log = LoggerFactory.getLogger(ConnectionsService.class);

    private final AnropsAdressRepository anropsAdressRepository;

    public ConnectionsService(Optional<AaaClient> aaaClient, AnropsAdressRepository anropsAdressRepository,
                              AaaConfig aaaConfig) {
        this.aaaClient = aaaClient;
        this.anropsAdressRepository = anropsAdressRepository;
        this.aaaConfig = aaaConfig;
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

    /**
     * Analyzes every not yet decided connection, one AAA request per unique URL. A failing request
     * only affects the connections using that URL: they are marked as failed with the error as
     * explanation, while all other rows keep their analysis result.
     */
    void applyAnalysisResult(List<ConnectionStatus> connectionStatus) {
        if (aaaClient.isEmpty()) {
            return;
        }
        AaaClient client = aaaClient.get();
        List<String> urls = connectionStatus.stream()
                .filter(cs -> cs.getSuccess() == null)
                .map(ConnectionStatus::getUrl)
                .sorted()
                .distinct()
                .toList();
        if (urls.isEmpty()) {
            return;
        }
        Map<String, AnalysisOutcome> outcomes = analyzeAll(client, urls);
        for (var status : connectionStatus) {
            AnalysisOutcome outcome = outcomes.get(status.getUrl());
            if (outcome == null) {
                continue;
            }
            if (outcome.result() != null) {
                status.setAnalysisResult(outcome.result());
            } else {
                status.analysisFailed(outcome.errorMessage());
            }
        }
    }

    private Map<String, AnalysisOutcome> analyzeAll(AaaClient client, List<String> urls) {
        int concurrency = concurrency(urls.size());
        Map<String, AnalysisOutcome> outcomes = new HashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        try {
            Map<String, Future<AnalysisOutcome>> futures = new LinkedHashMap<>();
            for (String url : urls) {
                futures.put(url, executor.submit(() -> analyzeOne(client, url)));
            }
            for (var entry : futures.entrySet()) {
                outcomes.put(entry.getKey(), awaitOutcome(entry.getKey(), entry.getValue()));
            }
        } finally {
            executor.shutdown();
        }
        return outcomes;
    }

    private AnalysisOutcome awaitOutcome(String url, Future<AnalysisOutcome> future) {
        try {
            return future.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return failure(url, ex);
        } catch (ExecutionException ex) {
            return failure(url, ex.getCause() != null ? ex.getCause() : ex);
        }
    }

    /**
     * Performs a single analysis. Any error is captured and returned as a failed outcome, so that
     * one broken URL never prevents the remaining connections from being presented.
     */
    private AnalysisOutcome analyzeOne(AaaClient client, String url) {
        try {
            AnalysisResultV1 result = client.analyze(url, ANALYZE_METHOD);
            log.debug("{} => {}", url, result);
            if (result == null) {
                return new AnalysisOutcome(null, "Tomt svar från AAA");
            }
            return new AnalysisOutcome(result, null);
        } catch (Exception ex) { // NOSONAR - deliberately broad: one bad URL must not break the page
            return failure(url, ex);
        }
    }

    private AnalysisOutcome failure(String url, Throwable ex) {
        log.warn("Analys av '{}' misslyckades: {}", url, ex);
        return new AnalysisOutcome(null, describe(ex));
    }

    static String describe(Throwable ex) {
        String message = ex.getMessage() == null || ex.getMessage().isBlank()
                ? ex.getClass().getSimpleName()
                : ex.getMessage();
        message = message.replaceAll("\\s+", " ").trim();
        return message.length() > MAX_ERROR_MESSAGE_LENGTH
                ? message.substring(0, MAX_ERROR_MESSAGE_LENGTH) + "…"
                : message;
    }

    /**
     * Number of parallel AAA requests. Misconfiguration is clamped instead of throwing, since a bad
     * value must not take the whole page down.
     */
    private int concurrency(int urlCount) {
        int configured = aaaConfig.getMaxConcurrentRequests();
        if (configured < 1) {
            log.warn("aaa.maxConcurrentRequests={} är ogiltigt, använder 1", configured);
            configured = 1;
        }
        return Math.min(configured, urlCount);
    }

    /** Result of analyzing one URL: either a result or an error message, never both. */
    record AnalysisOutcome(AnalysisResultV1 result, String errorMessage) {
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
        return new ConnectionStatus(anropsAdress.getTjanstekomponent().getHsaId(), address, aaaConfig.getUrl())
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
