/**
 * Copyright 2009 Sjukvardsradgivningen
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

import org.springframework.beans.factory.annotation.Autowired;

import se.skl.tp.vagvalsinfo.wsdl.v1.HamtaAllaAnropsBehorigheterResponseType;
import se.skl.tp.vagvalsinfo.wsdl.v1.HamtaAllaVirtualiseringarResponseType;

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
		assertEquals(3, result.getAnropsBehorighetsInfo().size());

		result = sokVagvalsInfoImpl.hamtaAllaAnropsBehorigheter("XXX");
		assertEquals(2, result.getAnropsBehorighetsInfo().size());

	}

}
