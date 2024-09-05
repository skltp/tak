/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
