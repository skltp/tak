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

import org.springframework.beans.factory.annotation.Autowired;

import se.skltp.tk.vagvalsinfo.wsdl.v1.HamtaAllaAnropsBehorigheterResponseType;
import se.skltp.tk.vagvalsinfo.wsdl.v1.HamtaAllaVirtualiseringarResponseType;

public class SokVagvalsInfoImplTest extends AbstractServiceTest {

	@Autowired
	SokVagvalsInfoImpl sokVagvalsInfoImpl;
	
	public void testHamtaAllaVirtualiseringar() {

		HamtaAllaVirtualiseringarResponseType result = sokVagvalsInfoImpl
				.hamtaAllaVirtualiseringar(null);
		assertEquals(3, result.getVirtualiseringsInfo().size());

		result = sokVagvalsInfoImpl.hamtaAllaVirtualiseringar("XXX");
		assertEquals(2, result.getVirtualiseringsInfo().size());

	}

	public void testhamtaAllaAnropsBehorigheter() {

		HamtaAllaAnropsBehorigheterResponseType result = sokVagvalsInfoImpl
				.hamtaAllaAnropsBehorigheter(null);
		assertEquals(6, result.getAnropsBehorighetsInfo().size());

		result = sokVagvalsInfoImpl.hamtaAllaAnropsBehorigheter("XXX");
		assertEquals(2, result.getAnropsBehorighetsInfo().size());

	}

}
