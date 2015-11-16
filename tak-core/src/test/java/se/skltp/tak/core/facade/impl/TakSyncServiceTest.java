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

	@Autowired
	TakSyncService takSyncService;
	
    public void testGetAllTjanstekomponent() throws Exception {
    	List<TjanstekomponentInfo> tkis = takSyncService.getAllTjanstekomponent();
    	assertEquals(8, tkis.size());
    	for (TjanstekomponentInfo tki : tkis) {

    		// service producers
    		if (tki.getHsaId().equals("hsa7")) {
    			assertEquals(1, tki.getAnropsAdressInfos().size());
    			AnropsAdressInfo aai = tki.getAnropsAdressInfos().get(0);
    			assertEquals("rivtabp20", aai.getRivtaProfilNamn());
    			assertEquals("http://path/to/some/address/rivtabp20", aai.getAdress());
    			 
    			assertEquals(3, aai.getVagvalsInfos().size());
    			for (VagvalsInfo vvi : aai.getVagvalsInfos()) {
    				assertEquals("test-hsa", vvi.getLogiskAdressHsaId());
    				assertEquals("Logisk adress HSAID TEST", vvi.getLogiskAdressBeskrivning());
    				assertTrue(vvi.getTjanstekontraktNamnrymd().equals("XXX")
    						|| vvi.getTjanstekontraktNamnrymd().equals("YYY"));
    				assertNotNull(vvi.getFromTidpunkt());
    				assertNotNull(vvi.getTomTidpunkt());
    				assertTrue("make sure from/to isn't mapped to the same field",
    						vvi.getFromTidpunkt().getTime() != vvi.getTomTidpunkt().getTime());
    			}
    		}
    		else if (tki.getHsaId().equals("hsa8")) {    			
    			assertEquals(1, tki.getAnropsAdressInfos().size());
    			AnropsAdressInfo aai = tki.getAnropsAdressInfos().get(0);
    			assertEquals("rivtabp21", aai.getRivtaProfilNamn());
    			assertEquals("http://path/to/some/address/rivtabp21", aai.getAdress());
    			assertEquals(0, aai.getVagvalsInfos().size());
    		}
    		else if (tki.getHsaId().equals("hsa-54")) { 
    			assertEquals(1, tki.getAnropsAdressInfos().size());
    		}
    		
    		// service consumers
    		else if (tki.getHsaId().equals("hsa2")) { 
    			assertEquals(1, tki.getAnropsbehorighetInfos().size());
    			AnropsbehorighetInfo abi = tki.getAnropsbehorighetInfos().get(0);
    			assertNotNull(abi.getFromTidpunkt());
    			assertNotNull(abi.getTomTidpunkt());
				assertTrue("make sure from/to isn't mapped to the same field",
						abi.getFromTidpunkt().getTime() != abi.getTomTidpunkt().getTime());
    			assertEquals("integrationsavtal_21", abi.getIntegrationsavtal());
    			assertEquals("XXX", abi.getTjanstekontraktNamnrymd());
    			assertEquals("Logisk adress HSAID TEST", abi.getLogiskAdressBeskrivning());
    			assertEquals("test-hsa", abi.getLogiskAdressHsaId());
    		}
    		else if (tki.getHsaId().equals("hsa3")) { 
    			assertEquals(3, tki.getAnropsbehorighetInfos().size());
    		}
    		else if (tki.getHsaId().equals("hsa4")) { 
    			assertEquals(2, tki.getAnropsbehorighetInfos().size());
    		}
    		else if (tki.getHsaId().equals("hsa5")) { 
    			assertEquals(0, tki.getAnropsbehorighetInfos().size());
    		}
    		else if (tki.getHsaId().equals("hsa-53")) { 
    			assertEquals(1, tki.getAnropsbehorighetInfos().size());
    		}
    		else {
    			fail("unexpected hsaId");
    		}
    	}
    	
    }

    public void testGetTjanstekontrakt() throws Exception {

        List<TjanstekontraktInfo> result = takSyncService.getAllTjanstekontrakt();
        assertEquals(6, result.size());

    }

	public void testGetAllVirtualisering() throws Exception {

		List<VirtualiseringInfo> result = takSyncService.getAllVagval();
		assertEquals(4, result.size());

	}
	public void testGetVirtualiseringByTjanstekontrakt() throws Exception {

		List<VirtualiseringInfo> result = takSyncService.getVagvalByTjanstekontrakt("XXX");
		assertEquals(2, result.size());

		result = takSyncService.getVagvalByTjanstekontrakt("YYY");
		assertEquals(1, result.size());

	}
	public void testGetAllAnropsbehorighet() throws Exception {
		List<AnropsbehorighetInfo> result = takSyncService.getAllAnropsbehorighet();
		assertEquals(7, result.size());
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
