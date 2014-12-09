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
package se.skltp.tak.core.facade.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.jpa.AbstractJpaTests;

import se.skltp.tak.core.facade.AnropsbehorighetInfo;
import se.skltp.tak.core.facade.TjanstekontraktInfo;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.core.facade.VirtualiseringInfo;

public class TakSyncServiceTest extends AbstractJpaTests {

	@Autowired
	TakSyncService takSyncService;

	@Autowired
	DataSource dataSource;
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath:tak-core-EMBED.xml" };
	}
	
	private String initialData = "<?xml version='1.0' encoding='UTF-8'?> "
			+ "<dataset>"
			+ "<tjanstekontrakt id='1' namnrymd='XXX' version='1' beskrivning='XXX kontrakt' majorVersion='1' minorVersion='0'/>"
			+ "<tjanstekontrakt id='2' namnrymd='YYY' version='1' beskrivning='YYY kontrakt' majorVersion='1' minorVersion='0'/>"
			+ "<tjanstekontrakt id='3' namnrymd='ZZZ' version='1' beskrivning='ZZZ kontrakt' majorVersion='1' minorVersion='0'/>"
			+ "<tjanstekontrakt id='4' namnrymd='AAA' version='1' beskrivning='AAA kontrakt' majorVersion='1' minorVersion='0'/>"
			+ "<tjanstekontrakt id='5' namnrymd='BBB' version='1' beskrivning='BBB kontrakt' majorVersion='1' minorVersion='0'/>"
			
			+ "<logiskadress id='3' hsaId='1' version='1' beskrivning='Logisk adress HSAID 1'/>"		
			+ "<logiskadress id='22' hsaId='test-hsa' version='1' beskrivning='Logisk adress HSAID TEST'/>"
			
			+ "<rivTaProfil id='4' namn='rivtabp20' version='1' beskrivning='RIV TA Profil 2.0'/>"
			+ "<rivTaProfil id='5' namn='rivtabp21' version='1' beskrivning='RIV TA Profil 2.1'/>"
			
			+ "<tjanstekomponent id='7' hsaId='hsa7' beskrivning='Tjanstekomponent HSAID 7' version='1'/>"
			+ "<tjanstekomponent id='8' hsaId='hsa8' beskrivning='Tjanstekomponent HSAID 8' version='1'/>"
			+ "<tjanstekomponent id='2' hsaId='hsa2' beskrivning='Tjanstekomponent HSAID 2' version='1'/>"
			+ "<tjanstekomponent id='3' hsaId='hsa3' beskrivning='Tjanstekomponent HSAID 3' version='1'/>"
			+ "<tjanstekomponent id='4' hsaId='hsa4' beskrivning='Tjanstekomponent HSAID 4' version='1'/>"
			
			+ "<anropsAdress id='1' adress='http://path/to/some/address/rivtabp20' version='1' rivTaProfil_id='4' tjanstekomponent_id='7'/>"
			+ "<anropsAdress id='2' adress='http://path/to/some/address/rivtabp21' version='1' rivTaProfil_id='5' tjanstekomponent_id='8'/>"
			
			+ "<vagval id='11' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' logiskadress_id='22' tjanstekontrakt_id='1' anropsAdress_id='1' version='1'/>"
			+ "<vagval id='12' fromTidpunkt='2010-12-24' tomTidpunkt='2011-12-24' logiskadress_id='22' tjanstekontrakt_id='1' anropsAdress_id='1' version='1'/>"
			+ "<vagval id='13' fromTidpunkt='2010-12-24' tomTidpunkt='2011-12-24' logiskadress_id='22' tjanstekontrakt_id='2' anropsAdress_id='1' version='1'/>"
			
			+ "<anropsBehorighet id='21' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='2' logiskadress_id='22' tjanstekontrakt_id='1' version='1' />"
			+ "<anropsBehorighet id='22' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='3' logiskadress_id='22' tjanstekontrakt_id='1' version='1' />"
			+ "<anropsBehorighet id='23' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='3' logiskadress_id='22' tjanstekontrakt_id='2' version='1' />"
			+ "<anropsBehorighet id='24' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='3' logiskadress_id='22' tjanstekontrakt_id='3' version='1' />"
			+ "<anropsBehorighet id='25' fromTidpunkt='2009-03-10' tomTidpunkt='2020-12-24' tjanstekonsument_id='4' logiskadress_id='22' tjanstekontrakt_id='4' version='1' />"
			+ "<anropsBehorighet id='26' fromTidpunkt='2009-03-10' tomTidpunkt='2020-12-24' tjanstekonsument_id='4' logiskadress_id='22' tjanstekontrakt_id='5' version='1' />"
			
			+ "<filter id='1' servicedomain='a_servicedomain' anropsbehorighet_id='24' version='1'/>"
			+ "<filtercategorization id='1' category='Booking' filter_id='1' version='1'/>"
			+ "<filtercategorization id='2' category='Invitation' filter_id='1' version='1'/>"
			+ "<filter id='2' servicedomain='Scheduling' anropsbehorighet_id='25' version='1'/>"
			+ "<filtercategorization id='3' category='Booking' filter_id='2' version='1'/>"
			+ "<filtercategorization id='4' category='Invitation' filter_id='2' version='1'/>"
			+ "<filter id='3' servicedomain='Servicedomain_FOO' anropsbehorighet_id='25' version='1'/>"
			+ "<filtercategorization id='5' category='Foo' filter_id='3' version='1'/>"
			+ "<filtercategorization id='6' category='Bar' filter_id='3' version='1'/>"
			+ "<filter id='4' servicedomain='Scheduling' anropsbehorighet_id='26' version='1'/>"

			+ "</dataset>";

	@Override
	protected void onSetUpInTransaction() throws Exception {
		TakSyncServiceTest.cleanInsert(dataSource, initialData);
	}
	
	private static void cleanInsert(DataSource dataSource, String dbUnitXML) throws Exception {
		InputStream fs = new ByteArrayInputStream(dbUnitXML.getBytes());
	    IDataSet dataSet = new FlatXmlDataSet(fs);
	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
	    DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

	}

    public void testGetTjanstekontrakt() throws Exception {

        List<TjanstekontraktInfo> result = takSyncService.getAllTjanstekontrakt();
        assertEquals(5, result.size());

    }

	public void testGetAllVirtualisering() throws Exception {

		List<VirtualiseringInfo> result = takSyncService.getAllVagval();
		assertEquals(3, result.size());

	}
	public void testGetVirtualiseringByTjanstekontrakt() throws Exception {

		List<VirtualiseringInfo> result = takSyncService.getVagvalByTjanstekontrakt("XXX");
		assertEquals(2, result.size());

		result = takSyncService.getVagvalByTjanstekontrakt("YYY");
		assertEquals(1, result.size());

	}
	public void testGetAllAnropsbehorighet() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getAllAnropsbehorighet();
		assertEquals(6, result.size());
	}
	
	public void testGetAnropsbehorighetByTjanstekontrakt() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getAnropsbehorighetByTjanstekontrakt("XXX");
		assertEquals(2, result.size());

		result = takSyncService.getAnropsbehorighetByTjanstekontrakt("YYY");
		assertEquals(1, result.size());
	}

	public void testLogicalAddressesAndFiltersByTjanstekontraktSingleFilter() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getLogicalAddresseesAndFiltersByServiceContract("ZZZ", "hsa3");
		assertNotNull(result.get(0).getFilterInfos());
		assertEquals(1, result.get(0).getFilterInfos().size());
		assertEquals("a_servicedomain", result.get(0).getFilterInfos().get(0).getServicedomain());
		assertNotNull(result.get(0).getFilterInfos().get(0).getFilterCategorizations());
		assertEquals(2, result.get(0).getFilterInfos().get(0).getFilterCategorizations().size());
	}
	
	public void testLogicalAddressesAndFiltersByTjanstekontraktWithMultipleFilters() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getLogicalAddresseesAndFiltersByServiceContract("AAA", "hsa4");
		assertNotNull(result.get(0).getFilterInfos());
		assertEquals(2, result.get(0).getFilterInfos().size());
	}
	
	public void testLogicalAddressesAndFiltersByTjanstekontraktWithSingleFilterNoCategorization() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getLogicalAddresseesAndFiltersByServiceContract("BBB", "hsa4");
		assertNotNull(result.get(0).getFilterInfos());
		assertEquals(1, result.get(0).getFilterInfos().size());
		assertNull(result.get(0).getFilterInfos().get(0).getFilterCategorizations());
	}
	
	public void testGetAllSupportedNamespacesByLogicalAddress() throws Exception {
		Set<String> result = takSyncService.getAllSupportedNamespacesByLogicalAddress("test-hsa", "hsa3");
		assertEquals(3, result.size());
		
		result = takSyncService.getAllSupportedNamespacesByLogicalAddress("test-hsa", "hsa2");
		assertEquals(1, result.size());
		
		result = takSyncService.getAllSupportedNamespacesByLogicalAddress("test-hsa", "hsa4");
		assertEquals(2, result.size());
		
		//Don't specify a serviceConsumerId and get all supported name spaces for the logical address
		result = takSyncService.getAllSupportedNamespacesByLogicalAddress("test-hsa", null);
		assertEquals(5, result.size());
	}
}
