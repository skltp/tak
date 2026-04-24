/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.FilterType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractResponseType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.GetLogicalAddresseesByServiceContractType;
import se.rivta.infrastructure.itintegration.registry.getlogicaladdresseesbyservicecontractresponder.v2.LogicalAddresseeRecordType;
import se.rivta.infrastructure.itintegration.registry.v2.ServiceContractNamespaceType;

@SpringBootTest
class GetLogicalAddresseesByServiceContractV2Test extends AbstractServiceTest {

	@Autowired
	GetLogicalAddresseesByServiceContractV2Impl glabsc;

	@Test
	void testAnropsbehorighetWithSingleFilterAndCategorizations() {

		final GetLogicalAddresseesByServiceContractType params = new GetLogicalAddresseesByServiceContractType();
		ServiceContractNamespaceType ns = new ServiceContractNamespaceType();
		ns.setServiceContractNamespace("urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1");
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("tp");

		final GetLogicalAddresseesByServiceContractResponseType labsc = this.glabsc
				.getLogicalAddresseesByServiceContract("", params);

		assertEquals(1, labsc.getLogicalAddressRecord().size());
		LogicalAddresseeRecordType firstLogicalAddresseeRecordType = labsc.getLogicalAddressRecord().get(0);
		assertEquals("5565594230", firstLogicalAddresseeRecordType.getLogicalAddress());
		
		FilterType firstFilterType = firstLogicalAddresseeRecordType.getFilter().get(0);
		assertEquals("urn:riv:itintegration:registry:GetItems", firstFilterType.getServiceDomain());
		
		String firstCategorization = firstFilterType.getCategorization().get(0);
		assertEquals("Category c1", firstCategorization);
	}
	
	@Test
	void testMultipleFilters() {

		final GetLogicalAddresseesByServiceContractType params = new GetLogicalAddresseesByServiceContractType();
		ServiceContractNamespaceType ns = new ServiceContractNamespaceType();
		ns.setServiceContractNamespace("urn:riv:itintegration:engagementindex:FindContentResponder:1");
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("tp");

		final GetLogicalAddresseesByServiceContractResponseType labsc = this.glabsc
				.getLogicalAddresseesByServiceContract("", params);

		assertEquals(1, labsc.getLogicalAddressRecord().size());
		LogicalAddresseeRecordType firstLogicalAddresseeRecordType = labsc.getLogicalAddressRecord().get(0);
		assertEquals("5565594230", firstLogicalAddresseeRecordType.getLogicalAddress());
		
		FilterType filterType = firstLogicalAddresseeRecordType.getFilter().get(0);
		assertEquals("urn:riv:itintegration:registry:GetItems", filterType.getServiceDomain());
		
		String categorization = filterType.getCategorization().get(0);
		assertEquals("Category c2", categorization);
		
		filterType = firstLogicalAddresseeRecordType.getFilter().get(1);
		assertEquals("urn:riv:itintegration:registry:GetMoreItems", filterType.getServiceDomain());
		
	}
	
	@Disabled
	public void testSingleFilterNoCategorization() {

		final GetLogicalAddresseesByServiceContractType params = new GetLogicalAddresseesByServiceContractType();
		ServiceContractNamespaceType ns = new ServiceContractNamespaceType();
		ns.setServiceContractNamespace("urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:1");
		params.setServiceContractNameSpace(ns);
		params.setServiceConsumerHsaId("tp");

		final GetLogicalAddresseesByServiceContractResponseType labsc = this.glabsc
				.getLogicalAddresseesByServiceContract("", params);

		assertEquals(1, labsc.getLogicalAddressRecord().size());
		LogicalAddresseeRecordType firstLogicalAddresseeRecordType = labsc.getLogicalAddressRecord().get(0);
		assertEquals("5565594230", firstLogicalAddresseeRecordType.getLogicalAddress());
		
		FilterType filterType = firstLogicalAddresseeRecordType.getFilter().get(0);
		assertEquals("urn:riv:itintegration:registry:GetItems", filterType.getServiceDomain());
		
		assertEquals(0, filterType.getCategorization().size());
		
	}
}
