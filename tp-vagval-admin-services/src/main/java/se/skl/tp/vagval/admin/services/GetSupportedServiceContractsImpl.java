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
package se.skl.tp.vagval.admin.services;

import java.util.Set;

import javax.jws.WebService;

import se.riv.itintegration.registry.getsupportedservicecontracts.v1.rivtabp21.GetSupportedServiceContractsResponderInterface;
import se.riv.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsResponseType;
import se.riv.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsType;
import se.riv.itintegration.registry.v1.ServiceContractNamespaceType;
import se.skl.tp.vagval.admin.core.facade.VagvalSyncService;

@WebService(
		serviceName = "GetSupportedServiceContractsResponderService",  
		portName = "GetSupportedServiceContractsResponderPort", 
		targetNamespace = "urn:riv:itintegration:registry:GetSupportedServiceContracts:1:rivtabp21")
public class GetSupportedServiceContractsImpl implements GetSupportedServiceContractsResponderInterface {

	private VagvalSyncService vagvalSyncService;
	
	public void setVagvalSyncService(final VagvalSyncService vagvalSyncService) {
		this.vagvalSyncService = vagvalSyncService;
	}
	
	@Override
	public GetSupportedServiceContractsResponseType getSupportedServiceContracts(
			String logicalAddress, GetSupportedServiceContractsType parameters) {
		
		final String addr = parameters.getLogicalAdress();
		if (addr == null || addr.trim().equals("")) {
			throw new IllegalArgumentException("LogicalAddress must not be empty or null");
		}

		final String consumerHsaId = parameters.getServiceConsumerHsaId();
		if (consumerHsaId == null || consumerHsaId.trim().equals("")) {
			throw new IllegalArgumentException("ServiceConsumerHsaId must not be empty or null");
		}

		final GetSupportedServiceContractsResponseType response = new GetSupportedServiceContractsResponseType();
		final Set<String> ns = this.vagvalSyncService.getAllSupportedNamespacesByLogicalAddress(addr, consumerHsaId);
		
		for (final String s : ns) {
			final ServiceContractNamespaceType sc = new ServiceContractNamespaceType();
			sc.setServiceContractNamespace(s);
			
			response.getServiceContractNamespace().add(sc);
		}
		
		return response;
	}

}
