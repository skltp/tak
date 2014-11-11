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
package se.skltp.tak.monitoring;

import org.junit.Test;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;

import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;
import se.skltp.tak.core.facade.VagvalSyncService;
import se.skltp.tak.monitoring.PingForConfigurationServiceImpl;
import se.skltp.tak.services.AbstractServiceTest;

public class PingForConfigurationServiceTest extends AbstractServiceTest{
	
	@Autowired
	PingForConfigurationServiceImpl serviceUnderTest;
	
	@Test
	public void testPingForConfiguration_ok() throws Exception {
		
		final PingForConfigurationType params = new PingForConfigurationType();
		params.setServiceContractNamespace("XXX");
		
		final PingForConfigurationResponseType response = this.serviceUnderTest.pingForConfiguration("logicalAddress", params);
		
		assertNotNull(response.getPingDateTime());
		assertEquals("Applikation", response.getConfiguration().get(0).getName());
		assertEquals("tk-admin-services", response.getConfiguration().get(0).getValue());
	}
	
	@Test
	public void testPingForConfiguration_db_service_not_available() throws Exception {
		
		
		VagvalSyncService vagvalSyncServiceMock = mock(VagvalSyncService.class);
		when(vagvalSyncServiceMock.getVirtualiseringByTjanstekontrakt(anyString())).thenThrow(new RuntimeException("Unchecked exception occured"));
		serviceUnderTest.setVagvalSyncService(vagvalSyncServiceMock);
		
		final PingForConfigurationType params = new PingForConfigurationType();
		params.setServiceContractNamespace("XXX");
		
		try {
			this.serviceUnderTest.pingForConfiguration("logicalAddress", params);	
			fail("Excpected RuntimeException due to errors connecting to database");
		} catch (Exception e) {
			assertEquals("Severe error in TK admin service database access, message: Unchecked exception occured", e.getMessage());
		}
	}
}
