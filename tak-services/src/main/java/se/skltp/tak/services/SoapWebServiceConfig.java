package se.skltp.tak.services;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.Bus;
import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.monitoring.PingForConfigurationServiceImpl;

@Configuration
public class SoapWebServiceConfig {

    private final TakSyncService takSyncService;  // Assuming you have TakSyncService as a dependency
    private final SoapRequestLogging incomingRequestsLogger;

    public SoapWebServiceConfig(TakSyncService takSyncService,
                                @Value("${tak.services.log-incoming-requests:false}") boolean logIncomingRequests) {
        this.takSyncService = takSyncService;
        this.incomingRequestsLogger = logIncomingRequests ? new SoapRequestLogging() : null;
    }

    @Bean
    public SoapRequestLogging incomingRequestLogging() {
        return new SoapRequestLogging();
    }

    @Bean
    public SokVagvalsInfoV2Impl sokVagvalsInfoV2() {
        SokVagvalsInfoV2Impl sokVagvalsInfoV2 = new SokVagvalsInfoV2Impl();
        sokVagvalsInfoV2.setTakSyncService(takSyncService);
        return sokVagvalsInfoV2;
    }

    @Bean
    public GetSupportedServiceContractsImpl getSupportedServiceContracts() {
        GetSupportedServiceContractsImpl service = new GetSupportedServiceContractsImpl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public GetSupportedServiceContractsV2Impl getSupportedServiceContractsV2() {
        GetSupportedServiceContractsV2Impl service = new GetSupportedServiceContractsV2Impl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public GetLogicalAddresseesByServiceContractV2Impl getLogicalAddresseesByServiceContractV2() {
        GetLogicalAddresseesByServiceContractV2Impl service = new GetLogicalAddresseesByServiceContractV2Impl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public PingForConfigurationServiceImpl pingForConfiguration() {
        PingForConfigurationServiceImpl service = new PingForConfigurationServiceImpl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public Endpoint pingForConfigurationEndpoint(Bus bus) {
        return makeMyEndpoint(bus,
                "/itintegration/monitoring/pingForConfiguration/1/rivtabp21",
                pingForConfiguration());
    }

    @Bean
    public Endpoint sokvagvalV2Endpoint(Bus bus) {
        return makeMyEndpoint(bus, "/SokVagvalsInfo/v2", sokVagvalsInfoV2());
    }

    @Bean
    public Endpoint getSupportedServiceContractsEndpoint(Bus bus) {
        return makeMyEndpoint(bus, "/GetSupportedServiceContracts", getSupportedServiceContracts());
    }

    @Bean
    public Endpoint getSupportedServiceContractsV2Endpoint(Bus bus) {
        return makeMyEndpoint(bus, "/GetSupportedServiceContracts/v2", getSupportedServiceContractsV2());
    }

    @Bean
    public Endpoint getLogicalAddresseesByServiceContractV2Endpoint(Bus bus) {
        return makeMyEndpoint(bus, "/GetLogicalAddresseesByServiceContract/v2", pingForConfiguration());
    }

    @Bean
    public Server jaxRsServerReset(TakSyncService takSyncService, TakPublishVersion takPublishVersion) {
        ResetPVCacheRESTService resetPVCacheRESTService = new ResetPVCacheRESTService(takSyncService, takPublishVersion);
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setServiceBean(resetPVCacheRESTService);
        factory.setAddress("/reset/pv");
        return factory.create();
    }

    @Bean
    public Server jaxRsServerExport(PubVersionDao pubVersionDao) {
        ExportTakData exportTakDataRESTService = new ExportTakData(pubVersionDao);
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setServiceBean(exportTakDataRESTService);
        factory.setAddress("/export/pv");
        return factory.create();
    }

    private Endpoint makeMyEndpoint(Bus bus, String path, Object svc) {
        EndpointImpl endpoint = new EndpointImpl(bus, svc);
        endpoint.publish(path);
        if (incomingRequestsLogger != null) {
            endpoint.getInInterceptors().add(incomingRequestsLogger);
        }
        return endpoint;
    }


}
