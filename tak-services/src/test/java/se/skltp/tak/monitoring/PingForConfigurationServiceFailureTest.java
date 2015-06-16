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

import se.riv.itintegration.monitoring.v1.PingForConfigurationType;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.monitoring.PingForConfigurationServiceImpl;
import se.skltp.tak.services.AbstractServiceTest;

public class PingForConfigurationServiceFailureTest extends AbstractServiceTest{
	
	@Autowired
	PingForConfigurationServiceImpl serviceUnderTest;
	
    @Autowired
    TakSyncService takSyncService;
	
    @Test
    public void testPingForConfiguration_db_service_not_available() throws Exception {
        
        final PingForConfigurationType params = new PingForConfigurationType();
        params.setServiceContractNamespace("xxx:yyy:zzz");
        
        try {
            TakSyncService takSyncServiceMock = mock(TakSyncService.class);
            when(takSyncServiceMock.getVagvalByTjanstekontrakt(anyString())).thenThrow(new RuntimeException("Unchecked exception occured (PingForConfigurationFailureTest)"));
            serviceUnderTest.setTakSyncService(takSyncServiceMock);
            serviceUnderTest.pingForConfiguration("logicalAddress", params);   
            fail("Expected RuntimeException due to errors connecting to database");
        } catch (Exception e) {
            assertEquals("Severe error in TK admin service database access, message: Unchecked exception occured (PingForConfigurationFailureTest)", e.getMessage());
        } finally {
            // reset the environment
            serviceUnderTest.setTakSyncService(takSyncService);
        }
    }
	
}
