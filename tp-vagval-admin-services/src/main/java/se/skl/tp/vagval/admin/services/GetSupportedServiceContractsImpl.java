/**
 * Copyright 2011 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
 */
package se.skl.tp.vagval.admin.services;

import java.util.Set;

import javax.jws.WebService;

import se.riv.itintegration.registry.getsupportedservicecontracts.v1.rivtabp21.GetSupportedServiceContractsResponderInterface;
import se.riv.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsResponseType;
import se.riv.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsType;
import se.riv.itintegration.registry.v1.ServiceContractType;
import se.skl.tp.vagval.admin.core.facade.VagvalSyncService;

@WebService(
		serviceName = "GetSupportedServiceContractsResponderService", 
		endpointInterface="se.riv.itintegration.registry.getsupportedservicecontracts.v1.rivtabp21.GetSupportedServiceContractsResponderInterface", 
		portName = "GetSupportedServiceContractsResponderPort", 
		targetNamespace = "urn:riv:itintegration:registry:GetSupportedServiceContracts:1:rivtabp21",
		wsdlLocation = "schemas/interactions/GetSupportedServiceContractsInteraction/GetSupportedServiceContractsInteraction_1.0_RIVTABP21.wsdl")
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
		
		final GetSupportedServiceContractsResponseType response = new GetSupportedServiceContractsResponseType();
		final Set<String> ns = this.vagvalSyncService.getAllSupportedNamespacesByLogicalAddress(addr);
		
		for (final String s : ns) {
			final ServiceContractType sc = new ServiceContractType();
			sc.setServiceContractNamespace(s);
			
			response.getServiceContractNamespaces().add(sc);
		}
		
		return response;
	}

}
