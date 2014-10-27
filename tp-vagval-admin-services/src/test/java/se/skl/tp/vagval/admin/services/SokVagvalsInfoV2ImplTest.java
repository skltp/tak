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

import se.skltp.tk.vagvalsinfo.wsdl.v2.*;


public class SokVagvalsInfoV2ImplTest extends AbstractServiceTest {

	@Autowired
	SokVagvalsInfoV2Impl sokVagvalsInfoV2Impl;

    public void testHamtaAllaTjanstekontrakt() {

        HamtaAllaTjanstekontraktResponseType result = sokVagvalsInfoV2Impl
                .hamtaAllaTjanstekontrakt(null);
        assertEquals(5, result.getTjanstekontraktInfo().size());

    }

    public void testHamtaAllaVirtualiseringar() {

        HamtaAllaVirtualiseringarResponseType result = sokVagvalsInfoV2Impl
                .hamtaAllaVirtualiseringar(null);
        assertEquals(3, result.getVirtualiseringsInfo().size());

        result = sokVagvalsInfoV2Impl.hamtaAllaVirtualiseringar("XXX");
        assertEquals(2, result.getVirtualiseringsInfo().size());

    }

    @Test
	public void testhamtaAllaAnropsBehorigheterAndTheirFilters() throws Exception {

		HamtaAllaAnropsBehorigheterResponseType result = sokVagvalsInfoV2Impl
				.hamtaAllaAnropsBehorigheter(null);
		
		assertEquals(6, result.getAnropsBehorighetsInfo().size());
		
		result = sokVagvalsInfoV2Impl.hamtaAllaAnropsBehorigheter("ZZZ");
		assertEquals(1, result.getAnropsBehorighetsInfo().size());		

		AnropsBehorighetsInfoType abInfoType = result.getAnropsBehorighetsInfo().get(0);
		assertEquals(1, abInfoType.getFilterInfo().size());
		
		FilterInfoType firstFilterInfoType = abInfoType.getFilterInfo().get(0);
		assertEquals("a_servicedomain", firstFilterInfoType.getServiceDomain());
		
		String firstCategorization = firstFilterInfoType.getCategorization().get(0);
		assertEquals("Booking", firstCategorization);
	}
}
