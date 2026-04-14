package se.skltp.tak.services;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.cxf.Bus;
import se.skltp.tak.core.dao.PubVersionDao;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.monitoring.PingForConfigurationServiceImpl;

@Configuration
public class WebServiceConfig {

    private final TakSyncService takSyncService;  // Assuming you have TakSyncService as a dependency
    private final SoapServicePublisher publisher;

    public WebServiceConfig(@Value("${tak.services.log-incoming-requests:false}")
                                boolean logIncomingRequests,
                            TakSyncService takSyncService,
                            Bus bus) {
        this.takSyncService = takSyncService;
        this.publisher = new SoapServicePublisher(bus, logIncomingRequests);
    }

    @Bean
    public PingForConfigurationServiceImpl pingForConfigurationServiceImpl() {
        PingForConfigurationServiceImpl service = new PingForConfigurationServiceImpl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public Endpoint pingForConfigurationEndpoint(PingForConfigurationServiceImpl service) {
        return publisher.publish("/itintegration/monitoring/pingForConfiguration/1/rivtabp21", service);
    }

    @Bean
    public SokVagvalsInfoV2Impl sokVagvalsInfoV2Impl() {
        SokVagvalsInfoV2Impl service = new SokVagvalsInfoV2Impl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public Endpoint sokvagvalV2Endpoint(SokVagvalsInfoV2Impl service) {
        return publisher.publish("/SokVagvalsInfo/v2", service);
    }

    @Bean
    public GetSupportedServiceContractsImpl getSupportedServiceContractsImpl() {
        GetSupportedServiceContractsImpl service = new GetSupportedServiceContractsImpl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public Endpoint getSupportedServiceContractsEndpoint(GetSupportedServiceContractsImpl service) {
        return publisher.publish("/GetSupportedServiceContracts", service);
    }

    @Bean
    public GetSupportedServiceContractsV2Impl getSupportedServiceContractsV2Impl() {
        GetSupportedServiceContractsV2Impl service = new GetSupportedServiceContractsV2Impl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public Endpoint getSupportedServiceContractsV2Endpoint(GetSupportedServiceContractsV2Impl service) {
        return publisher.publish("/GetSupportedServiceContracts/v2", service);
    }

    @Bean
    public GetLogicalAddresseesByServiceContractV2Impl getLogicalAddresseesByServiceContractV2Impl() {
        GetLogicalAddresseesByServiceContractV2Impl service = new GetLogicalAddresseesByServiceContractV2Impl();
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public Endpoint getLogicalAddresseesByServiceContractV2Endpoint(GetLogicalAddresseesByServiceContractV2Impl service) {
        return publisher.publish("/GetLogicalAddresseesByServiceContract/v2", service);
    }

    @Bean
    public Server jaxRsServerReset(TakPublishVersion takPublishVersion) {
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
}
