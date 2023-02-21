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
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.FilterInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaAnropsBehorigheterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaTjanstekomponenterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaTjanstekontraktResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaVirtualiseringarResponseType;


public class SokVagvalsInfoV2ImplTest extends AbstractServiceTest {

	@Autowired
	SokVagvalsInfoV2Impl sokVagvalsInfoV2Impl;

	public void testHamtaAllaTjanstekomponenter() {
		HamtaAllaTjanstekomponenterResponseType resultUsingNullParam = 
				sokVagvalsInfoV2Impl.hamtaAllaTjanstekomponenter(null);
		assertEquals(7, resultUsingNullParam.getTjanstekomponentInfo().size());		

		HamtaAllaTjanstekomponenterResponseType resultUsingObjectParam = 
				sokVagvalsInfoV2Impl.hamtaAllaTjanstekomponenter(new Object());
		assertEquals(7, resultUsingObjectParam.getTjanstekomponentInfo().size());
	}
	
    public void testHamtaAllaTjanstekontrakt() {

        HamtaAllaTjanstekontraktResponseType resultUsingNullParam = sokVagvalsInfoV2Impl
                .hamtaAllaTjanstekontrakt(null);
        assertEquals(7, resultUsingNullParam.getTjanstekontraktInfo().size());
        
        HamtaAllaTjanstekontraktResponseType resultUsingObjectParam = sokVagvalsInfoV2Impl
                .hamtaAllaTjanstekontrakt(new Object());
        assertEquals(7, resultUsingObjectParam.getTjanstekontraktInfo().size());

    }

    public void testHamtaAllaVirtualiseringar() {

        HamtaAllaVirtualiseringarResponseType resultUsingNullParam = sokVagvalsInfoV2Impl
                .hamtaAllaVirtualiseringar(null);
        assertEquals(9, resultUsingNullParam.getVirtualiseringsInfo().size());

        HamtaAllaVirtualiseringarResponseType resultUsingObjectParam = sokVagvalsInfoV2Impl
        		.hamtaAllaVirtualiseringar(new Object());
        assertEquals(9, resultUsingObjectParam.getVirtualiseringsInfo().size());

    }

    @Test
	public void testhamtaAllaAnropsBehorigheter() throws Exception {

		HamtaAllaAnropsBehorigheterResponseType resultUsingNullParam = sokVagvalsInfoV2Impl
				.hamtaAllaAnropsBehorigheter(null);
		assertEquals(8, resultUsingNullParam.getAnropsBehorighetsInfo().size());
		
		HamtaAllaAnropsBehorigheterResponseType resultUsingObjectParam = sokVagvalsInfoV2Impl
				.hamtaAllaAnropsBehorigheter(new Object());
		assertEquals(8, resultUsingObjectParam.getAnropsBehorighetsInfo().size());
	}
    
    @Test
   	public void testhamtaAnropsBehorighetAndTheirFilters() throws Exception {

   		HamtaAllaAnropsBehorigheterResponseType result = sokVagvalsInfoV2Impl
   				.hamtaAllaAnropsBehorigheter(null);
   		
   		assertEquals(8, result.getAnropsBehorighetsInfo().size());
   		
   		result = sokVagvalsInfoV2Impl.hamtaAllaAnropsBehorigheter(null);
   		assertEquals(8, result.getAnropsBehorighetsInfo().size());	
   		
   		AnropsBehorighetsInfoType anb = getAnropsBehorighetsInfoType("urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1", result);
   		
   		assertEquals(1, anb.getFilterInfo().size());
   		
   		FilterInfoType firstFilterInfoType = anb.getFilterInfo().get(0);
   		assertEquals("urn:riv:itintegration:registry:GetItems", firstFilterInfoType.getServiceDomain());
   		
   		String firstCategorization = firstFilterInfoType.getCategorization().get(0);
   		assertEquals("Category c1", firstCategorization);

   		
   	}
    
    private AnropsBehorighetsInfoType getAnropsBehorighetsInfoType(String namnrymd, HamtaAllaAnropsBehorigheterResponseType result){	
    	for (AnropsBehorighetsInfoType element : result.getAnropsBehorighetsInfo()) {
			if(element.getTjansteKontrakt().contains(namnrymd)){
				return element;
			}
		}
    	return null;
    }
}
