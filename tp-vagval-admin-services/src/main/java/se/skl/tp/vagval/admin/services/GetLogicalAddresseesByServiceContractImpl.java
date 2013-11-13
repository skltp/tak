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

import se.rivta.itintegration.registry.getlogicaladdresseesbyservicecontract.v1.rivtabp21.GetLogicalAddresseesByServiceContractResponderInterface;
import se.rivta.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v1.GetLogicalAddresseesByServiceContractResponseType;
import se.rivta.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v1.GetLogicalAddresseesByServiceContractType;
import se.rivta.itintegration.registry.v1.ServiceContractNamespaceType;
import se.skl.tp.vagval.admin.core.facade.VagvalSyncService;

@WebService(
		serviceName = "GetLogicalAddresseesByServiceContractResponderService", 
		portName = "GetLogicalAddresseesByServiceContractResponderPort", 
		targetNamespace = "urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContract:1:rivtabp21")
public class GetLogicalAddresseesByServiceContractImpl implements GetLogicalAddresseesByServiceContractResponderInterface {

	private VagvalSyncService vagvalSyncService;
	
	public void setVagvalSyncService(final VagvalSyncService vagvalSyncService) {
		this.vagvalSyncService = vagvalSyncService;
	}
	
	@Override
	public GetLogicalAddresseesByServiceContractResponseType getLogicalAddresseesByServiceContract(
			String logicalAddress,
			GetLogicalAddresseesByServiceContractType parameters) {
		
		final ServiceContractNamespaceType namespace = parameters.getServiceContractNameSpace();
		if (namespace == null || namespace.getServiceContractNamespace() == null || namespace.getServiceContractNamespace().trim().equals("")) {
			throw new IllegalArgumentException("ServiceContractNamespece must not be empty or null");
		}

		final String consumerHsaId = parameters.getServiceConsumerHsaId();
		if (consumerHsaId == null || consumerHsaId.trim().equals("")) {
			throw new IllegalArgumentException("ServiceConsumerHsaId must not be empty or null");
		}

		final GetLogicalAddresseesByServiceContractResponseType response = new GetLogicalAddresseesByServiceContractResponseType();
		final Set<String> ns = this.vagvalSyncService.getLogicalAddresseesByServiceContract(namespace.getServiceContractNamespace(), consumerHsaId);
		
		for (final String s : ns) {			
			response.getLogicalAddress().add(s);
		}

		return response;
	}
	
}
