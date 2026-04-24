/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.services;

import java.util.Date;
import java.util.Set;


import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.rivta.itintegration.registry.getsupportedservicecontracts.v1.rivtabp21.GetSupportedServiceContractsResponderInterface;
import se.rivta.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsResponseType;
import se.rivta.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsType;
import se.rivta.itintegration.registry.v1.ServiceContractNamespaceType;
import se.skltp.tak.core.facade.TakSyncService;

@WebService(
		serviceName = "GetSupportedServiceContractsResponderService",
		portName = "GetSupportedServiceContractsResponderPort",
		targetNamespace = "urn:riv:itintegration:registry:GetSupportedServiceContracts:1:rivtabp21")
public class GetSupportedServiceContractsImpl implements GetSupportedServiceContractsResponderInterface {

	private static final Logger log = LoggerFactory.getLogger(GetSupportedServiceContractsImpl.class);

	private TakSyncService takSyncService;

	public void setTakSyncService(final TakSyncService takSyncService) {
		this.takSyncService = takSyncService;
	}

	@Override
	public GetSupportedServiceContractsResponseType getSupportedServiceContracts(
			String logicalAddress, GetSupportedServiceContractsType parameters) {

		log.info("Request to tk-admin-services getSupportedServiceContracts v1");

		final String addr = parameters.getLogicalAdress();
		if (addr == null || addr.trim().equals("")) {
			throw new IllegalArgumentException("LogicalAddress must not be empty or null");
		}

		final String consumerHsaId = parameters.getServiceConsumerHsaId();
		if (consumerHsaId == null || consumerHsaId.trim().equals("")) {
			throw new IllegalArgumentException("ServiceConsumerHsaId must not be empty or null");
		}
		final Date now = new Date();

		final GetSupportedServiceContractsResponseType response = new GetSupportedServiceContractsResponseType();
		final Set<String> ns = this.takSyncService.getAllSupportedNamespacesByLogicalAddressAndDate(addr, consumerHsaId, now);

		for (final String s : ns) {
			final ServiceContractNamespaceType sc = new ServiceContractNamespaceType();
			sc.setServiceContractNamespace(s);

			response.getServiceContractNamespace().add(sc);
		}

		log.info("Response returned from tk-admin-services getSupportedServiceContracts v1");

		return response;
	}

}
