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

import riv.itintegration.registry.getlogicaladdresseesbyservicecontractresponder._1.GetLogicalAddresseesByServiceContractResponseType;
import riv.itintegration.registry.getlogicaladdresseesbyservicecontractresponder._1.GetLogicalAddresseesByServiceContractType;
import se.riv.itintegration.registry.v1.ServiceContractNamespaceType;

/**
 * Test of the get supported service contracts implementation
 * 
 * @author Mats Ekhammar [mats.ekhammar@callistaenterprise.se]
 */
public class GetLogicalAddresseesByServiceContractTest extends
		AbstractServiceTest {

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
