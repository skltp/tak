package se.skltp.tak.monitoring;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;
import se.skltp.tak.services.AbstractServiceTest;
import se.skltp.tak.services.TakServicesApplication;

@SpringBootTest(classes = TakServicesApplication.class)
class PingForConfigurationServiceTest extends AbstractServiceTest{
	
	@Autowired
	PingForConfigurationServiceImpl serviceUnderTest;
	
	@Test
	void testPingForConfiguration_ok() {
		
		final PingForConfigurationType params = new PingForConfigurationType();
		params.setServiceContractNamespace("aaa:bbb:ccc");
		
		final PingForConfigurationResponseType response = serviceUnderTest.pingForConfiguration("logicalAddress", params);
		
		assertNotNull(response.getPingDateTime());
		assertEquals("Applikation", response.getConfiguration().get(0).getName());
		assertEquals("tk-admin-services", response.getConfiguration().get(0).getValue());
	}
	
}
