package se.skltp.tak.services;

import java.util.Date;
import java.util.Set;

import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontracts.v2.rivtabp21.GetSupportedServiceContractsResponderInterface;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsResponseType;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsType;
import se.rivta.infrastructure.itintegration.registry.v2.ServiceContractNamespaceType;
import se.skltp.tak.core.facade.TakSyncService;

@WebService(
		serviceName = "GetSupportedServiceContractsResponderService",  
		portName = "GetSupportedServiceContractsResponderPort", 
		targetNamespace = "urn:riv:infrastructure:itintegration:registry:GetSupportedServiceContracts:2:rivtabp21")
public class GetSupportedServiceContractsV2Impl implements GetSupportedServiceContractsResponderInterface {
	
	private static final Logger log = LoggerFactory.getLogger(GetSupportedServiceContractsV2Impl.class);

	private TakSyncService takSyncService;
	
	public void setTakSyncService(final TakSyncService takSyncService) {
		this.takSyncService = takSyncService;
	}
	
	@Override
	public GetSupportedServiceContractsResponseType getSupportedServiceContracts(
			String logicalAddress, GetSupportedServiceContractsType parameters) {
		
		log.info("Request to tk-admin-services getSupportedServiceContracts v2");
		
		final String addr = parameters.getLogicalAdress();
		if (addr == null || addr.trim().equals("")) {
			throw new IllegalArgumentException("LogicalAddress must not be empty or null");
		}

		final String consumerHsaId = parameters.getServiceConsumerHsaId();
		final Date now = new Date();

		final GetSupportedServiceContractsResponseType response = new GetSupportedServiceContractsResponseType();
		final Set<String> ns = this.takSyncService.getAllSupportedNamespacesByLogicalAddressAndDate(addr, consumerHsaId, now);
		
		for (final String s : ns) {
			final ServiceContractNamespaceType sc = new ServiceContractNamespaceType();
			sc.setServiceContractNamespace(s);
			
			response.getServiceContractNamespace().add(sc);
		}
		
		log.info("Response returned from tk-admin-services getSupportedServiceContracts v2");
		
		return response;
	}

}
