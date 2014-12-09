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

import java.util.List;

import javax.jws.WebService;

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
			logicalAddresseeRecord.setLogicalAddress(info.getHsaIdLogiskAddresat());
			
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
