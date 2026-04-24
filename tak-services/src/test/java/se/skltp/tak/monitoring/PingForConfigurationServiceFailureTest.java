/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.monitoring;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.services.AbstractServiceTest;
import se.skltp.tak.services.TakServicesApplication;

@SpringBootTest(classes = {TakServicesApplication.class})
class PingForConfigurationServiceFailureTest extends AbstractServiceTest {
	
	@Autowired
	PingForConfigurationServiceImpl serviceUnderTest;
	
    @Autowired
    TakSyncService takSyncService;
	
    @Test
    void testPingForConfiguration_db_service_not_available() {
        
        final PingForConfigurationType params = new PingForConfigurationType();
        params.setServiceContractNamespace("xxx:yyy:zzz");
        
        try {
            TakSyncService takSyncServiceMock = mock(TakSyncService.class);
            when(takSyncServiceMock.getVagvalByTjanstekontrakt(anyString())).thenThrow(new RuntimeException("Unchecked exception occured (PingForConfigurationFailureTest)"));
            serviceUnderTest.setTakSyncService(takSyncServiceMock);
            serviceUnderTest.pingForConfiguration("logicalAddress", params);   
            fail("Expected RuntimeException due to errors connecting to database");
        } catch (Exception e) {
            assertEquals("Severe error in TK admin service access to TAK Cache, message: Unchecked exception occured (PingForConfigurationFailureTest)", e.getMessage());
        } finally {
            // reset the environment
            serviceUnderTest.setTakSyncService(takSyncService);
        }
    }
	
}
