package se.skl.tp.vagval.admin.services;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import se.riv.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsResponseType;
import se.riv.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsType;

/**
 * Test of the get supported service contracts
 * implementation
 * 
 * @author Marcus Krantz [marcus.krantz@callistaenterprise.se]
 */
public class GetSupportedServiceContractsTest extends AbstractServiceTest {

	@Autowired
	GetSupportedServiceContractsImpl gssc;
	
	@Test
	public void testGetSupportedServiceContracts() throws Exception {
		
		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress("test-hsa");
		
		final GetSupportedServiceContractsResponseType ssc = this.gssc.getSupportedServiceContracts("", params);
		
		assertEquals(2, ssc.getServiceContractNamespaces().size());
	}
	
	@Test
	public void testGetSupportedServiceContractsErrorOnNull() throws Exception {
		
		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress(null);
		
		GetSupportedServiceContractsResponseType ssc = null;
		try {
			ssc = this.gssc.getSupportedServiceContracts("", params);
			fail("Exception not thrown when logical address was null");
		} catch (final IllegalArgumentException e) {
			// OK
		}
		
		params.setLogicalAdress("");
		
		try {
			ssc = this.gssc.getSupportedServiceContracts("", params);
			fail("Exception not thrown when logical address was empty");
		} catch (final IllegalArgumentException e) {
			// OK
		}
	}
}
