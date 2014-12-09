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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.FilterType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractResponseType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.rivta.infrastructure.itintegration.registry.v2.ServiceContractNamespaceType;
import se.skltp.tak.services.GetLogicalAddresseesByServiceContractV2Impl;


public class GetLogicalAddresseesByServiceContractV2Test extends AbstractServiceTest {

	@Autowired
	GetLogicalAddresseesByServiceContractV2Impl glabsc;

	@Test
	public void testAnropsbehorighetWithSingleFilterAndCategorizations() throws Exception {

		final GetLogicalAddresseesByServiceContractType params = new GetLogicalAddresseesByServiceContractType();
		ServiceContractNamespaceType ns = new ServiceContractNamespaceType();
		ns.setServiceContractNamespace("ZZZ");
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("hsa3");

		final GetLogicalAddresseesByServiceContractResponseType labsc = this.glabsc
				.getLogicalAddresseesByServiceContract("", params);

		assertEquals(1, labsc.getLogicalAddressRecord().size());
		LogicalAddresseeRecordType firstLogicalAddresseeRecordType = labsc.getLogicalAddressRecord().get(0);
		assertEquals("test-hsa", firstLogicalAddresseeRecordType.getLogicalAddress());
		
		FilterType firstFilterType = firstLogicalAddresseeRecordType.getFilter().get(0);
		assertEquals("a_servicedomain", firstFilterType.getServiceDomain());
		
		String firstCategorization = firstFilterType.getCategorization().get(0);
		assertEquals("Booking", firstCategorization);
	}
	
	@Test
	public void testMultipleFilters() throws Exception {

		final GetLogicalAddresseesByServiceContractType params = new GetLogicalAddresseesByServiceContractType();
		ServiceContractNamespaceType ns = new ServiceContractNamespaceType();
		ns.setServiceContractNamespace("AAA");
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("hsa4");

		final GetLogicalAddresseesByServiceContractResponseType labsc = this.glabsc
				.getLogicalAddresseesByServiceContract("", params);

		assertEquals(1, labsc.getLogicalAddressRecord().size());
		LogicalAddresseeRecordType firstLogicalAddresseeRecordType = labsc.getLogicalAddressRecord().get(0);
		assertEquals("test-hsa", firstLogicalAddresseeRecordType.getLogicalAddress());
		
		FilterType filterType = firstLogicalAddresseeRecordType.getFilter().get(0);
		assertEquals("Scheduling", filterType.getServiceDomain());
		
		String categorization = filterType.getCategorization().get(0);
		assertEquals("Booking", categorization);
		
		filterType = firstLogicalAddresseeRecordType.getFilter().get(1);
		assertEquals("Servicedomain_FOO", filterType.getServiceDomain());
		
		categorization = filterType.getCategorization().get(1);
		assertEquals("Bar", categorization);
		
	}
	
	@Test
	public void testSingleFilterNoCategorization() throws Exception {

		final GetLogicalAddresseesByServiceContractType params = new GetLogicalAddresseesByServiceContractType();
		ServiceContractNamespaceType ns = new ServiceContractNamespaceType();
		ns.setServiceContractNamespace("BBB");
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("hsa4");

		final GetLogicalAddresseesByServiceContractResponseType labsc = this.glabsc
				.getLogicalAddresseesByServiceContract("", params);

		assertEquals(1, labsc.getLogicalAddressRecord().size());
		LogicalAddresseeRecordType firstLogicalAddresseeRecordType = labsc.getLogicalAddressRecord().get(0);
		assertEquals("test-hsa", firstLogicalAddresseeRecordType.getLogicalAddress());
		
		FilterType filterType = firstLogicalAddresseeRecordType.getFilter().get(0);
		assertEquals("Scheduling", filterType.getServiceDomain());
		
		assertEquals(0, filterType.getCategorization().size());
		
	}
	
}