/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.services;

import java.util.List;


import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontract.v2.rivtabp21.GetLogicalAddresseesByServiceContractResponderInterface;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.FilterType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractResponseType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.rivta.infrastructure.itintegration.registry.v2.ServiceContractNamespaceType;
import se.skltp.tak.core.facade.AnropsbehorighetInfo;
import se.skltp.tak.core.facade.FilterInfo;
import se.skltp.tak.core.facade.TakSyncService;

@WebService(serviceName = "GetLogicalAddresseesByServiceContractResponderService",
portName = "GetLogicalAddresseesByServiceContractResponderPort",
targetNamespace = "urn:riv:infrastructure:itintegration:registry:GetLogicalAddresseesByServiceContract:2:rivtabp21")
public class GetLogicalAddresseesByServiceContractV2Impl implements GetLogicalAddresseesByServiceContractResponderInterface {
	
	private static final Logger log = LoggerFactory.getLogger(GetLogicalAddresseesByServiceContractV2Impl.class);

	private TakSyncService takSyncService;

	public void setTakSyncService(final TakSyncService takSyncService) {
		this.takSyncService = takSyncService;
	}

	@Override
	public GetLogicalAddresseesByServiceContractResponseType getLogicalAddresseesByServiceContract(
			String logicalAddress,
			GetLogicalAddresseesByServiceContractType parameters) {
		
		log.info("Request to tk-admin-services getLogicalAddresseesByServiceContract v2");
		
		final ServiceContractNamespaceType namespace = parameters.getServiceContractNameSpace();
		if (namespace == null || namespace.getServiceContractNamespace() == null || namespace.getServiceContractNamespace().trim().equals("")) {
			throw new IllegalArgumentException("ServiceContractNamespece must not be empty or null");
		}

		final String consumerHsaId = parameters.getServiceConsumerHsaId();
		if (consumerHsaId == null || consumerHsaId.trim().equals("")) {
			throw new IllegalArgumentException("ServiceConsumerHsaId must not be empty or null");
		}

		final GetLogicalAddresseesByServiceContractResponseType response = new GetLogicalAddresseesByServiceContractResponseType();
		final List<AnropsbehorighetInfo> infos = this.takSyncService.getLogicalAddresseesAndFiltersByServiceContract(namespace.getServiceContractNamespace(), consumerHsaId);
		
		for(AnropsbehorighetInfo info : infos) {
			LogicalAddresseeRecordType logicalAddresseeRecord = new LogicalAddresseeRecordType();
			logicalAddresseeRecord.setLogicalAddress(info.getLogiskAdressHsaId());
			
			if(!info.getFilterInfos().isEmpty()) {
				for(FilterInfo filterInfo : info.getFilterInfos()) {
					FilterType filter = new FilterType();
					filter.setServiceDomain(filterInfo.getServicedomain());
					if(filterInfo.getFilterCategorizations() != null && !filterInfo.getFilterCategorizations().isEmpty()) {
						for(String categorization : filterInfo.getFilterCategorizations()) {
							filter.getCategorization().add(categorization);
						}
					}
					logicalAddresseeRecord.getFilter().add(filter);
				}
			}
			response.getLogicalAddressRecord().add(logicalAddresseeRecord);
		}
		
		log.info("Reponse returned from tk-admin-services getLogicalAddresseesByServiceContract v2");

		return response;
	}
}
