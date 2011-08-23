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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.riv.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsResponseType;
import se.riv.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsType;

/**
 * Test of the get supported service contracts
 * implementation
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 */
public class GetSupportedServiceContractsTest extends AbstractServiceTest {

	@Autowired
	GetSupportedServiceContractsImpl gssc;
	
	@Test
	public void testGetSupportedServiceContracts() throws Exception {
		
		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress("test-hsa");
		
		final GetSupportedServiceContractsResponseType ssc = this.gssc.getSupportedServiceContracts("", params);
		
		assertEquals(2, ssc.getServiceContractNamespaces().size());
	}
	
	@Test
	public void testGetSupportedServiceContractsErrorOnNull() throws Exception {
		
		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress(null);
		
		GetSupportedServiceContractsResponseType ssc = null;
		try {
			ssc = this.gssc.getSupportedServiceContracts("", params);
			fail("Exception not thrown when logical address was null");
		} catch (final IllegalArgumentException e) {
			// OK
		}
		
		params.setLogicalAdress("");
		
		try {
			ssc = this.gssc.getSupportedServiceContracts("", params);
			fail("Exception not thrown when logical address was empty");
		} catch (final IllegalArgumentException e) {
			// OK
		}
	}
}
