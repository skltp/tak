package se.skltp.tak.web.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import se.skltp.tak.web.aaa.client.model.AnalysisRequestV1;
import se.skltp.tak.web.aaa.client.model.AnalysisResultV1;
import se.skltp.tak.web.aaa.client.model.ConnectionChecklistV1;
import se.skltp.tak.web.client.AaaClient;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.dto.connection.ConnectionStatus;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    public static ConnectionChecklistV1 HTTP_SUCCESSFUL = new ConnectionChecklistV1()
            .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
            .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
            .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
            .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
            .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.UNKNOWN)
            .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.FAILURE)
            .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.UNKNOWN);
    public static ConnectionChecklistV1 HTTPS_SUCCESSFUL = new ConnectionChecklistV1()
            .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.SUCCESS)
            .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
            .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
            .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
            .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.SUCCESS)
            .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.SUCCESS)
            .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.SUCCESS);
    public static ConnectionChecklistV1 UNSUCCESSFUL = new ConnectionChecklistV1()
            .nameResolves(ConnectionChecklistV1.NameResolvesEnum.SUCCESS)
            .connectionOK(ConnectionChecklistV1.ConnectionOKEnum.FAILURE)
            .hostNameVerifiesOK(ConnectionChecklistV1.HostNameVerifiesOKEnum.SUCCESS)
            .tlsHandshakeOK(ConnectionChecklistV1.TlsHandshakeOKEnum.SUCCESS)
            .serverCertificateTrusted(ConnectionChecklistV1.ServerCertificateTrustedEnum.UNKNOWN)
            .serverAppliesMTLS(ConnectionChecklistV1.ServerAppliesMTLSEnum.FAILURE)
            .serverTrustsClientCertificate(ConnectionChecklistV1.ServerTrustsClientCertificateEnum.UNKNOWN);

    ConnectionsService service;

    @Autowired
    AnropsAdressRepository repository;

    @MockitoBean
    ConfigurationService configurationService;

    @MockitoBean
    AaaClient aaaClient;

    private AutoCloseable mocks;

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
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new ConnectionsService(Optional.of(aaaClient), repository);
    }

    @AfterEach
    public void teardown() throws Exception {
        mocks.close();
    }

    @Test
    void testGetActiveSuccessful() {
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

    private void configureAaaClient(ConnectionChecklistV1 httpChecklist, ConnectionChecklistV1 httpsChecklist) {
        when(aaaClient.analyze(any())).thenAnswer(invocation -> {
            List<AnalysisRequestV1> requests = invocation.getArgument(0 );
            List<AnalysisResultV1> results = new ArrayList<>();
            for (var request: requests) {
                var checklist = request.getUrl().startsWith("https") ? httpsChecklist : httpChecklist;
                AnalysisResultV1 result = new AnalysisResultV1()
                        .url(request.getUrl())
                        .connectionChecklist(checklist);
                results.add(result);
            }
            return results;
        });
    }
}
