package se.skltp.tak.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.FilterInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaAnropsBehorigheterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaTjanstekomponenterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaTjanstekontraktResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaVirtualiseringarResponseType;


@SpringBootTest(classes = {TakServicesApplication.class})
class SokVagvalsInfoV2ImplTest extends AbstractServiceTest {

	@Autowired
	SokVagvalsInfoV2Impl sokVagvalsInfoV2Impl;

	public void testHamtaAllaTjanstekomponenter() {
		HamtaAllaTjanstekomponenterResponseType resultUsingNullParam = 
				sokVagvalsInfoV2Impl.hamtaAllaTjanstekomponenter(null);
		assertEquals(7, resultUsingNullParam.getTjanstekomponentInfo().size());		

		HamtaAllaTjanstekomponenterResponseType resultUsingObjectParam = 
				sokVagvalsInfoV2Impl.hamtaAllaTjanstekomponenter(new Object());
		assertEquals(7, resultUsingObjectParam.getTjanstekomponentInfo().size());
	}
	
    public void testHamtaAllaTjanstekontrakt() {

        HamtaAllaTjanstekontraktResponseType resultUsingNullParam = sokVagvalsInfoV2Impl
                .hamtaAllaTjanstekontrakt(null);
        assertEquals(7, resultUsingNullParam.getTjanstekontraktInfo().size());
        
        HamtaAllaTjanstekontraktResponseType resultUsingObjectParam = sokVagvalsInfoV2Impl
                .hamtaAllaTjanstekontrakt(new Object());
        assertEquals(7, resultUsingObjectParam.getTjanstekontraktInfo().size());

    }

    public void testHamtaAllaVirtualiseringar() {

        HamtaAllaVirtualiseringarResponseType resultUsingNullParam = sokVagvalsInfoV2Impl
                .hamtaAllaVirtualiseringar(null);
        assertEquals(9, resultUsingNullParam.getVirtualiseringsInfo().size());

        HamtaAllaVirtualiseringarResponseType resultUsingObjectParam = sokVagvalsInfoV2Impl
        		.hamtaAllaVirtualiseringar(new Object());
        assertEquals(9, resultUsingObjectParam.getVirtualiseringsInfo().size());

    }

    @Test
	void testhamtaAllaAnropsBehorigheter() {

		HamtaAllaAnropsBehorigheterResponseType resultUsingNullParam = sokVagvalsInfoV2Impl
				.hamtaAllaAnropsBehorigheter(null);
		assertEquals(8, resultUsingNullParam.getAnropsBehorighetsInfo().size());
		
		HamtaAllaAnropsBehorigheterResponseType resultUsingObjectParam = sokVagvalsInfoV2Impl
				.hamtaAllaAnropsBehorigheter(new Object());
		assertEquals(8, resultUsingObjectParam.getAnropsBehorighetsInfo().size());
	}
    
    @Test
	void testhamtaAnropsBehorighetAndTheirFilters() {

   		HamtaAllaAnropsBehorigheterResponseType result = sokVagvalsInfoV2Impl
   				.hamtaAllaAnropsBehorigheter(null);
   		
   		assertEquals(8, result.getAnropsBehorighetsInfo().size());
   		
   		result = sokVagvalsInfoV2Impl.hamtaAllaAnropsBehorigheter(null);
   		assertEquals(8, result.getAnropsBehorighetsInfo().size());	
   		
   		AnropsBehorighetsInfoType anb = getAnropsBehorighetsInfoType("urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1", result);
   		
   		assertEquals(1, anb.getFilterInfo().size());
   		
   		FilterInfoType firstFilterInfoType = anb.getFilterInfo().get(0);
   		assertEquals("urn:riv:itintegration:registry:GetItems", firstFilterInfoType.getServiceDomain());
   		
   		String firstCategorization = firstFilterInfoType.getCategorization().get(0);
   		assertEquals("Category c1", firstCategorization);

   		
   	}
    
    private AnropsBehorighetsInfoType getAnropsBehorighetsInfoType(String namnrymd, HamtaAllaAnropsBehorigheterResponseType result){	
    	for (AnropsBehorighetsInfoType element : result.getAnropsBehorighetsInfo()) {
			if(element.getTjansteKontrakt().contains(namnrymd)){
				return element;
			}
		}
    	return null;
    }
}
