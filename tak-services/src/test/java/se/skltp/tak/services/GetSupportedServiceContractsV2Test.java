package se.skltp.tak.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsResponseType;
import se.rivta.infrastructure.itintegration.registry.getsupportedservicecontractsresponder.v2.GetSupportedServiceContractsType;


@SpringBootTest(classes = {TakServicesApplication.class})
class GetSupportedServiceContractsV2Test extends AbstractServiceTest {

	@Autowired
	GetSupportedServiceContractsV2Impl gssc;

	@Test
	void testGetSupportedServiceContracts() {

		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress("HSA-VKK123");
		params.setServiceConsumerHsaId("tp");

		final GetSupportedServiceContractsResponseType ssc = this.gssc.getSupportedServiceContracts("", params);

		assertEquals(1, ssc.getServiceContractNamespace().size());
	}

	@Test
	void testGetSupportedServiceContractsWithoutServiceConsumerHsaId() {

		final GetSupportedServiceContractsType params = new GetSupportedServiceContractsType();
		params.setLogicalAdress("5565594230");

		final GetSupportedServiceContractsResponseType ssc = this.gssc.getSupportedServiceContracts("", params);

		assertEquals(4, ssc.getServiceContractNamespace().size());
	}
}
