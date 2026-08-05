/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;
import se.skltp.tak.web.aaa.client.model.ConnectionChecklistV1;
import se.skltp.tak.web.client.AaaClient;
import se.skltp.tak.web.configuration.AaaConfig;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings("HttpUrlsUsage") // HTTP links used in test
@DataJpaTest
public class ConnectionsServiceTest {

    // Database:
    //
    // * Vagval '2013-08-24', '2113-08-24'
    //    * adress: 'http://localhost:8083/GetAggregatedSubjectOfCareSchedule/service/v1'
    //    * Tjanstekomponent: 7 'AGT-TIDBOK'
    //* Vagval '2013-08-24', '2113-08-24'
    //    * adress: 'http://localhost:8081/skltp-ei/notification-service/v1'
    //    * Tjanstekomponent: 4 'EI-HSAID'
    //* Vagval '2013-08-24', '2113-08-24'
    //    * adress: 'http://localhost:8081/skltp-ei/update-service/v1'
    //    * Tjanstekomponent: 4 'EI-HSAID'
    //* Vagval '2013-08-24', '2113-08-24'
    //    * adress: 'http://localhost:8082/skltp-ei/find-content-service/v1'
    //    * Tjanstekomponent: 4 'EI-HSAID'
    //* Vagval '2025-05-24', '2125-05-24'
    //    * adress: 'http://localhost:8088/NyaServiceURL/service/v1'
    //    * Tjanstekomponent: 4 'EI-HSAID'
    //* Vagval '2013-05-24', '2113-05-24'
    //    * adress: 'http://33.33.33.33:8080/Schedulr-0.1/ws/GetSubjectOfCareSchedule/1'
    //    * Tjanstekomponent: 1 'SCHEDULR'
    //* Vagval '2013-05-28', '2113-05-28'
    //    * adress: 'http://localhost:10000/test/Ping_Service'
    //    * Tjanstekomponent: 2 'TP'
    //* Vagval '2013-08-24', '2113-08-24'
    //    * adress: 'https://localhost:23001/vp/GetLogicalAddresseesByServiceContract/1/rivtabp21'
    //    * Tjanstekomponent: 5 'VP-CACHAD-GETLOGICALADDRESSEESBYSERVICECONTRACT'

    public static final ConnectionChecklistV1 HTTP_SUCCESSFUL = new ConnectionChecklistV1()
            .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
            .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
            .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
            .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
            .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.UNKNOWN)
            .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.FAILURE)
            .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.UNKNOWN);
    public static final ConnectionChecklistV1 HTTPS_SUCCESSFUL = new ConnectionChecklistV1()
            .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
            .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
            .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
            .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
            .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.SUCCESS)
            .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.SUCCESS)
            .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.SUCCESS);
    public static final ConnectionChecklistV1 UNSUCCESSFUL = new ConnectionChecklistV1()
            .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
            .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.FAILURE)
            .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
            .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
            .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.UNKNOWN)
            .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.FAILURE)
            .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.UNKNOWN);

    @Autowired
    AnropsAdressRepository repository;

    @MockitoBean
    ConfigurationService configurationService;

    @MockitoBean
    AaaClient aaaClient;

    private AutoCloseable mocks;

    private AaaConfig aaaConfig;


    public static Stream<Arguments> httpsNotSuccessful() {
        return Stream.of(
                Arguments.of(new ConnectionChecklistV1()
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.FAILURE)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.SUCCESS)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.SUCCESS)),
                Arguments.of(new ConnectionChecklistV1()
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.FAILURE)
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.SUCCESS)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.SUCCESS)),
                Arguments.of(new ConnectionChecklistV1()
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.FAILURE)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.SUCCESS)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.SUCCESS)),
                Arguments.of(new ConnectionChecklistV1()
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.FAILURE)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.SUCCESS)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.SUCCESS)),
                Arguments.of(new ConnectionChecklistV1()
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.FAILURE)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.SUCCESS)),
                Arguments.of(new ConnectionChecklistV1()
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.SUCCESS)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.FAILURE)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.SUCCESS)),
                Arguments.of(new ConnectionChecklistV1()
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.SUCCESS)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.FAILURE))
        );
    }

    public static Stream<Arguments> httpNotSuccessful() {
        return Stream.of(
                Arguments.of(new ConnectionChecklistV1()
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.FAILURE)
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.UNKNOWN)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.FAILURE)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.UNKNOWN)),
                Arguments.of(new ConnectionChecklistV1()
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.FAILURE)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.UNKNOWN)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.FAILURE)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.UNKNOWN)),
                Arguments.of(new ConnectionChecklistV1()
                        .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
                        .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
                        .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.FAILURE)
                        .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
                        .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.UNKNOWN)
                        .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.FAILURE)
                        .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.UNKNOWN))
        );
    }

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        aaaConfig = new AaaConfig();
    }

    @AfterEach
    void teardown() throws Exception {
        mocks.close();
    }

    @Test
    void testRestApiAvailable() {
        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        assertTrue(service.isAvailable());
    }

    @Test
    void testRestApiNotAvailable() {
        var service = new ConnectionsService(Optional.empty(), repository, aaaConfig);
        assertFalse(service.isAvailable());
    }

    @Test
    void testGetActiveSuccessful() {
        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        configureAaaClient(HTTP_SUCCESSFUL, HTTPS_SUCCESSFUL);
        PagedEntityList<ConnectionStatus> page = service.getActive(0, 3);
        assertEquals(3, page.getSize());
        List<ConnectionStatus> connectionStatuses = page.getContent();
        assertEquals("AGT-TIDBOK", connectionStatuses.get(0).getHsaId());
        assertEquals("http://localhost:8083", connectionStatuses.get(0).getUrl());
        assertTrue(connectionStatuses.get(0).getSuccess());
        assertEquals("EI-HSAID", connectionStatuses.get(1).getHsaId());
        assertEquals("http://localhost:8081", connectionStatuses.get(1).getUrl());
        assertTrue(connectionStatuses.get(1).getSuccess());
        assertEquals("EI-HSAID", connectionStatuses.get(2).getHsaId());
        assertEquals("http://localhost:8082", connectionStatuses.get(2).getUrl());
        assertTrue(connectionStatuses.get(2).getSuccess());
        page = service.getActive(3, 3);
        assertEquals(3, page.getSize());
        connectionStatuses = page.getContent();
        assertEquals("EI-HSAID", connectionStatuses.get(0).getHsaId());
        assertEquals("http://localhost:8088", connectionStatuses.get(0).getUrl());
        assertTrue(connectionStatuses.get(0).getSuccess());
        assertEquals("SCHEDULR", connectionStatuses.get(1).getHsaId());
        assertEquals("http://33.33.33.33:8080", connectionStatuses.get(1).getUrl());
        assertTrue(connectionStatuses.get(1).getSuccess());
        assertEquals("TP", connectionStatuses.get(2).getHsaId());
        assertEquals("http://localhost:10000", connectionStatuses.get(2).getUrl());
        assertTrue(connectionStatuses.get(2).getSuccess());
        page = service.getActive(6, 3);
        assertEquals(1, page.getSize());
        connectionStatuses = page.getContent();
        assertEquals("VP-CACHAD-GETLOGICALADDRESSEESBYSERVICECONTRACT", connectionStatuses.get(0).getHsaId());
        assertEquals("https://localhost:23001", connectionStatuses.get(0).getUrl());
        assertTrue(connectionStatuses.get(0).getSuccess());
    }

    @ParameterizedTest
    @MethodSource("httpsNotSuccessful")
    void testGetActiveHttpsNotSuccessful(ConnectionChecklistV1 httpsUnsuccessful) {
        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        configureAaaClient(UNSUCCESSFUL, httpsUnsuccessful);
        PagedEntityList<ConnectionStatus> page = service.getActive(0, 10);
        assertEquals(7, page.getSize());
        List<ConnectionStatus> connectionStatuses = page.getContent();
        assertEquals("AGT-TIDBOK", connectionStatuses.get(0).getHsaId());
        assertEquals("http://localhost:8083", connectionStatuses.get(0).getUrl());
        assertFalse(connectionStatuses.get(0).getSuccess());
        assertEquals("EI-HSAID", connectionStatuses.get(1).getHsaId());
        assertEquals("http://localhost:8081", connectionStatuses.get(1).getUrl());
        assertFalse(connectionStatuses.get(1).getSuccess());
        assertEquals("EI-HSAID", connectionStatuses.get(2).getHsaId());
        assertEquals("http://localhost:8082", connectionStatuses.get(2).getUrl());
        assertFalse(connectionStatuses.get(2).getSuccess());
        assertEquals("EI-HSAID", connectionStatuses.get(3).getHsaId());
        assertEquals("http://localhost:8088", connectionStatuses.get(3).getUrl());
        assertFalse(connectionStatuses.get(3).getSuccess());
        assertEquals("SCHEDULR", connectionStatuses.get(4).getHsaId());
        assertEquals("http://33.33.33.33:8080", connectionStatuses.get(4).getUrl());
        assertFalse(connectionStatuses.get(4).getSuccess());
        assertEquals("TP", connectionStatuses.get(5).getHsaId());
        assertEquals("http://localhost:10000", connectionStatuses.get(5).getUrl());
        assertFalse(connectionStatuses.get(5).getSuccess());
        assertEquals("VP-CACHAD-GETLOGICALADDRESSEESBYSERVICECONTRACT", connectionStatuses.get(6).getHsaId());
        assertEquals("https://localhost:23001", connectionStatuses.get(6).getUrl());
        assertFalse(connectionStatuses.get(6).getSuccess());
    }

    @ParameterizedTest
    @MethodSource("httpNotSuccessful")
    void testGetActiveHttpNotSuccessful(ConnectionChecklistV1 httpUnsuccessful) {
        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        configureAaaClient(httpUnsuccessful, UNSUCCESSFUL);
        PagedEntityList<ConnectionStatus> page = service.getActive(0, 10);
        assertEquals(7, page.getSize());
        List<ConnectionStatus> connectionStatuses = page.getContent();
        assertEquals("AGT-TIDBOK", connectionStatuses.get(0).getHsaId());
        assertEquals("http://localhost:8083", connectionStatuses.get(0).getUrl());
        assertFalse(connectionStatuses.get(0).getSuccess());
        assertEquals("EI-HSAID", connectionStatuses.get(1).getHsaId());
        assertEquals("http://localhost:8081", connectionStatuses.get(1).getUrl());
        assertFalse(connectionStatuses.get(1).getSuccess());
        assertEquals("EI-HSAID", connectionStatuses.get(2).getHsaId());
        assertEquals("http://localhost:8082", connectionStatuses.get(2).getUrl());
        assertFalse(connectionStatuses.get(2).getSuccess());
        assertEquals("EI-HSAID", connectionStatuses.get(3).getHsaId());
        assertEquals("http://localhost:8088", connectionStatuses.get(3).getUrl());
        assertFalse(connectionStatuses.get(3).getSuccess());
        assertEquals("SCHEDULR", connectionStatuses.get(4).getHsaId());
        assertEquals("http://33.33.33.33:8080", connectionStatuses.get(4).getUrl());
        assertFalse(connectionStatuses.get(4).getSuccess());
        assertEquals("TP", connectionStatuses.get(5).getHsaId());
        assertEquals("http://localhost:10000", connectionStatuses.get(5).getUrl());
        assertFalse(connectionStatuses.get(5).getSuccess());
        assertEquals("VP-CACHAD-GETLOGICALADDRESSEESBYSERVICECONTRACT", connectionStatuses.get(6).getHsaId());
        assertEquals("https://localhost:23001", connectionStatuses.get(6).getUrl());
        assertFalse(connectionStatuses.get(6).getSuccess());
    }

    @ParameterizedTest
    @MethodSource("connectionStatusAnropsAdress")
    void testToConnectionStatus(String inAdress, String expectedOutAdress, Boolean success) {
        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        AnropsAdress anropsAdress = getAnropsAdress("AAAA", inAdress);

        var connectionStatus = service.toConnectionStatus(anropsAdress);

        assertEquals(expectedOutAdress, connectionStatus.getUrl());
        assertEquals("AAAA", connectionStatus.getHsaId());
        assertEquals(success, connectionStatus.getSuccess());
    }

    @Test
    void testPagination() {
        AnropsAdressRepository dummyRepository = mock(AnropsAdressRepository.class);
        when(dummyRepository.findActive()).thenReturn(createAnropsAdressList());
        var service = new ConnectionsService(Optional.empty(), dummyRepository, aaaConfig);

        PagedEntityList<ConnectionStatus> list = service.getActive(0, 10);
        assertEquals(11, list.getTotalElements());
        assertEquals(2, list.getTotalPages());
        assertEquals(10, list.getSize());
        list = service.getActive(10, 10);
        assertEquals(11, list.getTotalElements());
        assertEquals(2, list.getTotalPages());
        assertEquals(1, list.getSize());
    }

    @Test
    void testApplyAnalysisResultCallsAaaOncePerUniqueUrl() {
        List<ConnectionStatus> statuses = createConnectionStatusList(25);
        stubAaaClientEchoingRequests();

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        verify(aaaClient, times(25)).analyze(any(), eq("HEAD"));
        assertAllUrlsAnalyzed(statuses);
    }

    @Test
    void testApplyAnalysisResultDoesNotRepeatCallsForDuplicateUrls() {
        // Two producers sharing the same base address => a single AAA request
        List<ConnectionStatus> statuses = List.of(
                new ConnectionStatus("hsa1", "http://host:80", ""),
                new ConnectionStatus("hsa2", "http://host:80", ""));
        stubAaaClientEchoingRequests();

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        verify(aaaClient, times(1)).analyze("http://host:80", "HEAD");
        assertAllUrlsAnalyzed(statuses);
    }

    @Test
    void testApplyAnalysisResultWithLowConcurrencyStillAnalyzesEveryUrl() {
        List<ConnectionStatus> statuses = createConnectionStatusList(10);
        stubAaaClientEchoingRequests();
        aaaConfig.setMaxConcurrentRequests(3);

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        verify(aaaClient, times(10)).analyze(any(), eq("HEAD"));
        assertAllUrlsAnalyzed(statuses);
    }

    @Test
    void testApplyAnalysisResultMarksOnlyFailingRowWhenOneUrlFails() {
        List<ConnectionStatus> statuses = createConnectionStatusList(3);
        String failingUrl = statuses.get(1).getUrl();
        when(aaaClient.analyze(any(), any())).thenAnswer(invocation -> {
            String url = invocation.getArgument(0);
            if (url.equals(failingUrl)) {
                throw new IllegalStateException("[400] Ogiltig URL");
            }
            return new AnalysisResultV1().url(url).connectionChecklist(HTTP_SUCCESSFUL);
        });

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        assertTrue(statuses.get(0).getSuccess());
        assertNull(statuses.get(0).getAnalysisError());
        assertFalse(statuses.get(1).getSuccess(), "Failing URL must be marked as failed");
        assertEquals("[400] Ogiltig URL", statuses.get(1).getAnalysisError());
        assertNull(statuses.get(1).getAnalysisResult());
        assertTrue(statuses.get(2).getSuccess());
    }

    @Test
    void testApplyAnalysisResultMarksAllRowsSharingAFailingUrl() {
        List<ConnectionStatus> statuses = List.of(
                new ConnectionStatus("hsa1", "http://host:80", ""),
                new ConnectionStatus("hsa2", "http://host:80", ""));
        when(aaaClient.analyze(any(), any())).thenThrow(new RuntimeException("Connection refused"));

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        for (ConnectionStatus cs : statuses) {
            assertFalse(cs.getSuccess());
            assertEquals("Connection refused", cs.getAnalysisError());
        }
    }

    @Test
    void testApplyAnalysisResultHandlesEmptyResponse() {
        List<ConnectionStatus> statuses = createConnectionStatusList(1);
        when(aaaClient.analyze(any(), any())).thenReturn(null);

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        assertFalse(statuses.get(0).getSuccess());
        assertEquals("Tomt svar från AAA", statuses.get(0).getAnalysisError());
    }

    @Test
    void testApplyAnalysisResultUsesClassNameWhenExceptionHasNoMessage() {
        List<ConnectionStatus> statuses = createConnectionStatusList(1);
        when(aaaClient.analyze(any(), any())).thenThrow(new IllegalStateException());

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        assertEquals("IllegalStateException", statuses.get(0).getAnalysisError());
    }

    @Test
    void testApplyAnalysisResultTruncatesLongErrorMessages() {
        List<ConnectionStatus> statuses = createConnectionStatusList(1);
        when(aaaClient.analyze(any(), any()))
                .thenThrow(new RuntimeException("x".repeat(ConnectionsService.MAX_ERROR_MESSAGE_LENGTH + 50)));

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        String error = statuses.get(0).getAnalysisError();
        assertEquals(ConnectionsService.MAX_ERROR_MESSAGE_LENGTH + 1, error.length());
        assertTrue(error.endsWith("…"));
    }

    @Test
    void testApplyAnalysisResultNoCallWhenNoUrlsToAnalyze() {
        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        // success already set => filtered out, so no requests remain
        service.applyAnalysisResult(List.of(new ConnectionStatus("hsa", "http://host:80", "").success(false)));

        verify(aaaClient, never()).analyze(any(), any());
    }


    @Test
    void testApplyAnalysisResultNoCallsWhenClientAbsent() {
        List<ConnectionStatus> statuses = createConnectionStatusList(25);

        var service = new ConnectionsService(Optional.empty(), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        verifyNoInteractions(aaaClient);
        for (ConnectionStatus cs : statuses) {
            assertNull(cs.getSuccess());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void testApplyAnalysisResultClampsNonPositiveConcurrency(int invalidSize) {
        // A bad configuration must not take the page down, it is clamped to a single thread
        List<ConnectionStatus> statuses = createConnectionStatusList(3);
        aaaConfig.setMaxConcurrentRequests(invalidSize);
        stubAaaClientEchoingRequests();

        var service = new ConnectionsService(Optional.of(aaaClient), repository, aaaConfig);
        service.applyAnalysisResult(statuses);

        verify(aaaClient, times(3)).analyze(any(), eq("HEAD"));
        assertAllUrlsAnalyzed(statuses);
    }

    private List<ConnectionStatus> createConnectionStatusList(int count) {
        List<ConnectionStatus> statuses = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            statuses.add(new ConnectionStatus("hsa" + i, "http://host" + i + ":80", ""));
        }
        return statuses;
    }

    private void stubAaaClientEchoingRequests() {
        when(aaaClient.analyze(any(), any())).thenAnswer(invocation -> {
            String url = invocation.getArgument(0);
            assertEquals("HEAD", invocation.getArgument(1));
            return new AnalysisResultV1().url(url).connectionChecklist(HTTP_SUCCESSFUL);
        });
    }

    private void assertAllUrlsAnalyzed(List<ConnectionStatus> statuses) {
        for (ConnectionStatus cs : statuses) {
            assertNotNull(cs.getAnalysisResult(), "Missing analysis result for " + cs.getUrl());
            assertEquals(cs.getUrl(), cs.getAnalysisResult().getUrl());
            assertNull(cs.getAnalysisError());
            assertTrue(cs.getSuccess(), "Expected success=true for " + cs.getUrl());
        }
    }

    @Test
    void testGetEntityName() {
        var service = new ConnectionsService(Optional.empty(), repository, aaaConfig);
        assertEquals("Anslutningar", service.getEntityName());
    }

    public static Stream<String> correctUrls() {
        return Stream.of(
            "https://example.com:443",
            "http://example.com:80",
            "https://example.com:443/some/path",
            "https://example.com:443/some/path?and=query&parameters",
            "http://192.168.0.1:80",
            "http://192.168.1.10:8080/dashboard",
            "https://192.168.100.5:443/api?verbose=true",
            "http://[2001:db8::1]:80/",
            "https://[2001:db8:abcd:0012::100]:8443/api/v2/system",
            "http://[fe80::1%25eth0]:8080/status"
        );
    }

    public static Stream<String> badUrls() {
        return Stream.of(
            "https://exa mple.com:1234",
            "https://exam|ple.com:1234",
            "https://exa<mple>.com:1234",
            "1http://example.com:1234",
            "ht^tp://example.com:1234",
            "https://example.com:port",
            "https://example.com:999999",
            "http://[2001:db8:::1]:1234/",
            "http://[2001:db8:zzzz::1]:1234/",
            "https://:80",
            "https://@example.com:1234",
            "https:///path",
            "https://example.com:1234/path%2",
            "https://example.com:1234/%GG%00",
            "https://example.com:1234/pa th",
            "https://example.com:1234/pa<th>",
            "https://example.com:1234/?q=foo bar",
            "https://例え.com:1234/path",
            "https://examp­le.com:1234",
            "https://user:password@example.com:1234/resource"
        );
    }

    @ParameterizedTest
    @MethodSource("correctUrls")
    void testCheckUrl(String input) {
        var service = new ConnectionsService(Optional.empty(), repository, aaaConfig);
        assertTrue(service.checkUrl(input));
    }

    @ParameterizedTest
    @MethodSource("badUrls")
    void testCheckUrlBadUrls(String input) {
        var service = new ConnectionsService(Optional.empty(), repository, aaaConfig);
        assertFalse(service.checkUrl(input));
    }

    private List<AnropsAdress> createAnropsAdressList() {
        List<AnropsAdress> anropsAdressList = new ArrayList<>();
        for (int ctr = 0; ctr < 101; ctr++) {
            int hundreds = ctr / 100;
            int num = hundreds * 100 + (ctr % 10);
            AnropsAdress anropsAdress = getAnropsAdress(String.format("%d", num), String.format("http://%d.%d.%d.%d:%d/aaa", num, num, num, num, num % 10));
            anropsAdressList.add(anropsAdress);
        }
        return anropsAdressList;
    }

    private void configureAaaClient(ConnectionChecklistV1 httpChecklist, ConnectionChecklistV1 httpsChecklist) {
        when(aaaClient.analyze(any(), any())).thenAnswer(invocation -> {
            String url = invocation.getArgument(0);
            var checklist = url.startsWith("https") ? httpsChecklist : httpChecklist;
            return new AnalysisResultV1()
                    .url(url)
                    .connectionChecklist(checklist);
        });
    }

    private AnropsAdress getAnropsAdress(String hsaId, String adress) {
        Tjanstekomponent tjanstekomponent = new Tjanstekomponent();
        tjanstekomponent.setHsaId(hsaId);
        AnropsAdress anropsAdress = new AnropsAdress();
        anropsAdress.setAdress(adress);
        anropsAdress.setTjanstekomponent(tjanstekomponent);
        return anropsAdress;
    }

    private static Stream<Arguments> connectionStatusAnropsAdress() {
        return Stream.of(
            Arguments.of("incorrectlyFormattedAddress", "incorrectlyformattedaddress", false),
            Arguments.of("Https://example.com:443", "https://example.com:443", null),
            Arguments.of("https://example.com", "https://example.com:443", null),
            Arguments.of("http://localhost", "http://localhost:80", null),
            Arguments.of("https://exa mple.com:1234", "https://exa mple.com:1234", false)
        );
    }
}
