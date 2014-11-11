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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsResponseType;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsType;


public class GetSupportedServiceContractsV2Test extends AbstractServiceTest {

	@Autowired
	GetSupportedServiceContractsV2Impl gssc;
	
	@Test
	public void testGetSupportedServiceContracts() throws Exception {
		
		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress("test-hsa");
		params.setServiceConsumerHsaId("hsa2");
		
		final GetSupportedServiceContractsResponseType ssc = this.gssc.getSupportedServiceContracts("", params);
		
		assertEquals(1, ssc.getServiceContractNamespace().size());
	}
	
	@Test
	public void testGetSupportedServiceContractsWithoutServiceConsumerHsaId() throws Exception {
		
		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress("test-hsa");
		
		final GetSupportedServiceContractsResponseType ssc = this.gssc.getSupportedServiceContracts("", params);
		
		assertEquals(5, ssc.getServiceContractNamespace().size());
	}
}
