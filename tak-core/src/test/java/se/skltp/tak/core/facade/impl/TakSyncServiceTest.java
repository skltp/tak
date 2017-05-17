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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import se.skltp.tak.core.facade.AnropsAdressInfo;
import se.skltp.tak.core.facade.AnropsbehorighetInfo;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.core.facade.TjanstekomponentInfo;
import se.skltp.tak.core.facade.TjanstekontraktInfo;
import se.skltp.tak.core.facade.VagvalsInfo;
import se.skltp.tak.core.facade.VirtualiseringInfo;

public class TakSyncServiceTest extends AbstractCoreTest {

	public static final String DATE_2017_05_12 = "2017-05-12";
	public static final String DATE_2008_01_02 = "2008-01-02";
	public static final String DATE_2199_01_02 = "2199-01-02";
	@Autowired
	TakSyncService takSyncService;
	
    public void testGetAllTjanstekomponent() throws Exception {
    	List<TjanstekomponentInfo> tkis = takSyncService.getAllTjanstekomponent();
    	assertEquals(7, tkis.size());
    	for (TjanstekomponentInfo tki : tkis) {

    		// service producers
    		if (tki.getHsaId().equals("Schedulr")) {
    			assertEquals(1, tki.getAnropsAdressInfos().size());
    			AnropsAdressInfo aai = tki.getAnropsAdressInfos().get(0);
    			assertEquals("RIVTABP21", aai.getRivtaProfilNamn());
    			assertEquals("http://33.33.33.33:8080/Schedulr-0.1/ws/GetSubjectOfCareSchedule/1", aai.getAdress());
    			 
    			assertEquals(3, aai.getVagvalsInfos().size());
    			for (VagvalsInfo vvi : aai.getVagvalsInfos()) {
    				assertTrue( vvi.getLogiskAdressHsaId().equals("HSA-VKK123") ||
    						    vvi.getLogiskAdressHsaId().equals("HSA-VKM345") ||
    						    vvi.getLogiskAdressHsaId().equals("HSA-VKY567"));
    				assertTrue( vvi.getLogiskAdressBeskrivning().equals("Demo adressat tidbok, vardcentralen kusten, Karna") ||
    						    vvi.getLogiskAdressBeskrivning().equals("Demo adressat tidbok, vardcentralen kusten, Marstrand") ||
    						    vvi.getLogiskAdressBeskrivning().equals("Demo adressat tidbok, vardcentralen kusten, Ytterby"));
    				assertTrue( vvi.getTjanstekontraktNamnrymd().equals("urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1"));
    				assertNotNull(vvi.getFromTidpunkt());
    				assertNotNull(vvi.getTomTidpunkt());
    				assertTrue("make sure from/to isn't mapped to the same field",
    						vvi.getFromTidpunkt().getTime() != vvi.getTomTidpunkt().getTime());
    			}
    		}
    		else if (tki.getHsaId().equals("PING-HSAID")) {    			
    			assertEquals(0, tki.getAnropsAdressInfos().size());
    		}
    		else if (tki.getHsaId().equals("EI-HSAID")) { 
    			assertEquals(3, tki.getAnropsAdressInfos().size());
    		}
    		else if (tki.getHsaId().equals("VP-Cachad-GetLogicalAddresseesByServiceContract")) { 
    			assertEquals(1, tki.getAnropsAdressInfos().size());
    		}
    		else if (tki.getHsaId().equals("AGT-Tidbok")) { 
    			assertEquals(1, tki.getAnropsAdressInfos().size());
    		}
    		
    		// service consumers
    		else if (tki.getHsaId().equals("tp")) { 
    			assertEquals(8, tki.getAnropsbehorighetInfos().size());
    			AnropsbehorighetInfo abi = tki.getAnropsbehorighetInfos().get(0);
    			assertNotNull(abi.getFromTidpunkt());
    			assertNotNull(abi.getTomTidpunkt());
				assertTrue("make sure from/to isn't mapped to the same field",
						abi.getFromTidpunkt().getTime() != abi.getTomTidpunkt().getTime());
    			assertEquals("I1", abi.getIntegrationsavtal());
    			assertEquals("urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1", abi.getTjanstekontraktNamnrymd());
    			assertEquals("Demo adressat tidbok, vardcentralen kusten, Karna", abi.getLogiskAdressBeskrivning());
    			assertEquals("HSA-VKK123", abi.getLogiskAdressHsaId());
    		}
    		else if (tki.getHsaId().equals("5565594230")) { 
    			assertEquals(0, tki.getAnropsbehorighetInfos().size());
    		}
    		else if (tki.getHsaId().equals("HSA-NYA-Test-123")) { 
    			assertEquals(1, tki.getAnropsbehorighetInfos().size());
    		}
    		else {
    			System.out.println(tki.getHsaId());
    			fail("unexpected hsaId");
    		}
    	}
    }

    public void testGetTjanstekontrakt() throws Exception {

        List<TjanstekontraktInfo> result = takSyncService.getAllTjanstekontrakt();
        assertEquals(7, result.size());

    }

	public void testGetAllVirtualisering() throws Exception {

		List<VirtualiseringInfo> result = takSyncService.getAllVagval();
		assertEquals(9, result.size());

	}
	
	public void testGetVirtualiseringByTjanstekontrakt() throws Exception {

		List<VirtualiseringInfo> result = takSyncService.getVagvalByTjanstekontrakt("urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1");
		assertEquals(3, result.size());

		result = takSyncService.getVagvalByTjanstekontrakt("urn:riv:itinfra:tp:PingResponder:1");
		assertEquals(1, result.size());

	}
	
	public void testGetAllAnropsbehorighet() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getAllAnropsbehorighet();
		assertEquals(8, result.size());
	}
	
	public void testGetAnropsbehorighetByTjanstekontrakt() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getAnropsbehorighetByTjanstekontrakt("urn:riv:itinfra:tp:PingResponder:1");
		assertEquals(2, result.size());

		result = takSyncService.getAnropsbehorighetByTjanstekontrakt("urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1");
		assertEquals(1, result.size());
	}

	public void testLogicalAddressesAndFiltersByTjanstekontraktSingleFilter() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getLogicalAddresseesAndFiltersByServiceContract("urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1", "tp");
		assertNotNull(result.get(0).getFilterInfos());
		assertEquals(1, result.get(0).getFilterInfos().size());
		assertEquals("urn:riv:itintegration:registry:GetItems", result.get(0).getFilterInfos().get(0).getServicedomain());
		assertNotNull(result.get(0).getFilterInfos().get(0).getFilterCategorizations());
		assertEquals(2, result.get(0).getFilterInfos().get(0).getFilterCategorizations().size());
	}
		
	public void testLogicalAddressesAndFiltersByTjanstekontraktWithMultipleFilters() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getLogicalAddresseesAndFiltersByServiceContract("urn:riv:itintegration:engagementindex:FindContentResponder:1", "tp");
		assertNotNull(result.get(0).getFilterInfos());
		assertEquals(2, result.get(0).getFilterInfos().size());
	}
	
	public void testLogicalAddressesAndFiltersByTjanstekontraktWithSingleFilterNoCategorization() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getLogicalAddresseesAndFiltersByServiceContract("urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:1", "tp");
		assertNotNull(result.get(0).getFilterInfos());
		assertEquals(1, result.get(0).getFilterInfos().size());
		assertNull(result.get(0).getFilterInfos().get(0).getFilterCategorizations());
	}

	public void testGetAllSupportedNamespacesByLogicalAddress() throws Exception {
		Set<String> result = takSyncService.getAllSupportedNamespacesByLogicalAddress("5565594230", "tp");
		assertEquals(4, result.size());
		
		result = takSyncService.getAllSupportedNamespacesByLogicalAddress("HSA-VKK123", "tp");
		assertEquals(1, result.size());
		
		result = takSyncService.getAllSupportedNamespacesByLogicalAddress("PING", "tp");
		assertEquals(1, result.size());
		
		//Don't specify a serviceConsumerId and get all supported name spaces for the logical address
		result = takSyncService.getAllSupportedNamespacesByLogicalAddress("5565594230", null);
		assertEquals(4, result.size());
	}

	public void testGetAllSupportedNamespacesByLogicalAddressAndDate() throws Exception {
    	Date aDayInMay2017 = stringToDate(DATE_2017_05_12);

		Set<String> result = takSyncService.getAllSupportedNamespacesByLogicalAddressAndDate("5565594230", "tp", aDayInMay2017);
		assertEquals(4, result.size());

		result = takSyncService.getAllSupportedNamespacesByLogicalAddressAndDate("5565594230", "tp", stringToDate(DATE_2008_01_02) );
		assertEquals(0, result.size());

		result = takSyncService.getAllSupportedNamespacesByLogicalAddressAndDate("5565594230", "tp", stringToDate(DATE_2199_01_02));
		assertEquals(0, result.size());

		result = takSyncService.getAllSupportedNamespacesByLogicalAddressAndDate("HSA-VKK123", "tp", aDayInMay2017);
		assertEquals(1, result.size());

		result = takSyncService.getAllSupportedNamespacesByLogicalAddressAndDate("PING", "tp", aDayInMay2017);
		assertEquals(1, result.size());

		//Don't specify a serviceConsumerId and get all supported name spaces for the logical address
		result = takSyncService.getAllSupportedNamespacesByLogicalAddressAndDate("5565594230", null, null);
		assertEquals(4, result.size());
	}

	private Date stringToDate(String date) throws ParseException {
		DateFormat formatter ;
		formatter = new SimpleDateFormat("yy-MM-dd");
		return formatter.parse(date);
	}
}
