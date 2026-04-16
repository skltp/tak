package se.skltp.tak.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import se.rivta.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsResponseType;
import se.rivta.itintegration.registry.getsupportedservicecontractsresponder.v1.GetSupportedServiceContractsType;

/**
 * Test of the "get supported service contracts" implementation.
 */
@SpringBootTest(classes = {TakServicesApplication.class})
class GetSupportedServiceContractsTest extends AbstractServiceTest {

	@Autowired
	GetSupportedServiceContractsImpl gssc;

	@Test
	void testGetSupportedServiceContracts() {

		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress("5565594230");
		params.setServiceConsumerHsaId("tp");

		final GetSupportedServiceContractsResponseType ssc = this.gssc.getSupportedServiceContracts("", params);

		assertEquals(4, ssc.getServiceContractNamespace().size());
	}

	@Test
	void testGetSupportedServiceContractsErrorOnNull() {

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
