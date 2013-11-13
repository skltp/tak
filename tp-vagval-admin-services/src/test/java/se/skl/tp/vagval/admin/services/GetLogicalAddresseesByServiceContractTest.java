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

import se.rivta.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v1.GetLogicalAddresseesByServiceContractResponseType;
import se.rivta.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v1.GetLogicalAddresseesByServiceContractType;
import se.rivta.itintegration.registry.v1.ServiceContractNamespaceType;

/**
 * Test of the get supported service contracts implementation
 * 
 * @author Mats Ekhammar [mats.ekhammar@callistaenterprise.se]
 */
public class GetLogicalAddresseesByServiceContractTest extends AbstractServiceTest  {

	@Autowired
	GetLogicalAddresseesByServiceContractImpl glabsc;

	@Test
	public void testGetLogicalAddresseesByServiceContract() throws Exception {

		final GetLogicalAddresseesByServiceContractType params = new GetLogicalAddresseesByServiceContractType();
		ServiceContractNamespaceType ns = new ServiceContractNamespaceType();
		ns.setServiceContractNamespace("XXX");
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("hsa2");

		final GetLogicalAddresseesByServiceContractResponseType labsc = this.glabsc
				.getLogicalAddresseesByServiceContract("", params);

		assertEquals(1, labsc.getLogicalAddress().size());
	}

	@Test
	public void testGetLogicalAddresseesByServiceContractErrorOnNull()
			throws Exception {

		final GetLogicalAddresseesByServiceContractType params = new GetLogicalAddresseesByServiceContractType();
		ServiceContractNamespaceType ns = new ServiceContractNamespaceType();
		ns.setServiceContractNamespace(null);
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("hsa2");

		GetLogicalAddresseesByServiceContractResponseType labsc = null;
		try {
			labsc = this.glabsc.getLogicalAddresseesByServiceContract("",
					params);
			fail("Exception not thrown when namespace was null");
		} catch (final IllegalArgumentException e) {
			// OK
		}

		ns.setServiceContractNamespace("");
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("1");

		try {
			labsc = this.glabsc.getLogicalAddresseesByServiceContract("",
					params);
			fail("Exception not thrown when namespace was empty");
		} catch (final IllegalArgumentException e) {
			// OK
		}
	}
}
