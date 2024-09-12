package se.skltp.tak.services;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.Bus;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.monitoring.PingForConfigurationServiceImpl;

@Configuration
public class SoapWebServiceConfig {

    private final TakSyncService takSyncService;  // Assuming you have TakSyncService as a dependency

    public SoapWebServiceConfig(TakSyncService takSyncService) {
        this.takSyncService = takSyncService;
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
    public ResetPVCacheRESTService resetPVCache(TakPublishVersion takPublishVersion) {
        ResetPVCacheRESTService service = new ResetPVCacheRESTService();
        service.setTakPublishVersion(takPublishVersion);
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public GetApplicationStatus getAppInfoBean(TakPublishVersion takPublishVersion) {
        GetApplicationStatus service = new GetApplicationStatus();
        service.setTakPublishVersion(takPublishVersion);
        service.setTakSyncService(takSyncService);
        return service;
    }

    @Bean
    public Endpoint sokvagvalV2Endpoint(Bus bus) {
        EndpointImpl endpoint = new EndpointImpl(bus, sokVagvalsInfoV2());
        endpoint.publish("/SokVagvalsInfo/v2");
        return endpoint;
    }

    @Bean
    public Endpoint getSupportedServiceContractsEndpoint(Bus bus) {
        EndpointImpl endpoint = new EndpointImpl(bus, getSupportedServiceContracts());
        endpoint.publish("/GetSupportedServiceContracts");
        return endpoint;
    }

    @Bean
    public Endpoint getSupportedServiceContractsV2Endpoint(Bus bus) {
        EndpointImpl endpoint = new EndpointImpl(bus, getSupportedServiceContractsV2());
        endpoint.publish("/GetSupportedServiceContracts/v2");
        return endpoint;
    }

    @Bean
    public JAXRSServerFactoryBean resetPVCacheService() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setServiceBean(resetPVCache(null));  // Pass the required instance
        factory.setAddress("/reset");
        factory.setProvider(new JSONProvider<>());
        return factory;
    }

    @Bean
    public JAXRSServerFactoryBean getAppInfoService() {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setServiceBean(getAppInfoBean(null));  // Pass the required instance
        factory.setAddress("/appinfo");
        factory.setProvider(new JSONProvider<>());
        return factory;
    }
}
