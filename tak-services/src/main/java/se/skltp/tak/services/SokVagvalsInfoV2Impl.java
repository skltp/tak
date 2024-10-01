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
package se.skltp.tak.services;

import java.util.List;


import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.skltp.tak.core.facade.AnropsAdressInfo;
import se.skltp.tak.core.facade.AnropsbehorighetInfo;
import se.skltp.tak.core.facade.FilterInfo;
import se.skltp.tak.core.facade.TjanstekomponentInfo;
import se.skltp.tak.core.facade.TjanstekontraktInfo;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.core.facade.VagvalsInfo;
import se.skltp.tak.core.facade.VirtualiseringInfo;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsAdressInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoIdType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.AnropsbehorighetInfoForTjanstekomponentType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.FilterInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaAnropsBehorigheterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaTjanstekomponenterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaTjanstekontraktResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaVirtualiseringarResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.SokVagvalsInfoInterface;
import se.skltp.tak.vagvalsinfo.wsdl.v2.TjanstekomponentInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.TjanstekontraktInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.VagvalsInfoType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.VirtualiseringsInfoIdType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.VirtualiseringsInfoType;

@WebService(portName = "SokVagvalsSoap11LitDocPort", serviceName = "SokVagvalsServiceSoap11LitDocService", targetNamespace = "urn:skl:tp:vagvalsinfo:v2")
public class SokVagvalsInfoV2Impl implements SokVagvalsInfoInterface {
	
	private static final Logger log = LoggerFactory.getLogger(SokVagvalsInfoV2Impl.class);

	TakSyncService takSyncService;

	public void setTakSyncService(TakSyncService takSyncService) {
		this.takSyncService = takSyncService;
	}

    /**
     * Hamta en lista av alla tjanstekontrakt.
     *
     * @param parameters
     *            - null, eftersom operationen inte har någon payload.
     */
    @Override
    public HamtaAllaTjanstekontraktResponseType hamtaAllaTjanstekontrakt(Object parameters) {
        log.info("Request to tk-admin-services hamtaAllaTjanstekontrakt v2, par: {}", (parameters == null) ? null : parameters.getClass().getName());

        HamtaAllaTjanstekontraktResponseType response = new HamtaAllaTjanstekontraktResponseType();

        List<TjanstekontraktInfo> tjanstekontrakt = takSyncService.getAllTjanstekontrakt();

        for (TjanstekontraktInfo tk : tjanstekontrakt) {

            TjanstekontraktInfoType tkType = new TjanstekontraktInfoType();
            
            tkType.setNamnrymd(tk.getNamnrymd());
            tkType.setMajorVersion(tk.getMajorVersion());
            tkType.setMinorVersion(tk.getMinorVersion());
            tkType.setBeskrivning(tk.getBeskrivning());

            response.getTjanstekontraktInfo().add(tkType);
        }

        log.info("Response returned from tk-admin-services hamtaAllaTjanstekontrakt v2, size: {}",
				response.getTjanstekontraktInfo().size());
        return response;
    }

    /**
	 * Hamta en lista av alla anropsbehörigheter.
	 * 
	 * @param parameters
	 *            - null, eftersom operationen inte har någon payload.
	 */
	public HamtaAllaAnropsBehorigheterResponseType hamtaAllaAnropsBehorigheter(Object parameters) {
		
		log.info("Request to tk-admin-services hamtaAllaAnropsBehorigheter v2");

		HamtaAllaAnropsBehorigheterResponseType response = new HamtaAllaAnropsBehorigheterResponseType();

		List<AnropsbehorighetInfo> anropsbehorigheter = takSyncService.getAllAnropsbehorighetAndFilter();

		for (AnropsbehorighetInfo ab : anropsbehorigheter) {

			AnropsBehorighetsInfoType abType = new AnropsBehorighetsInfoType();

			abType.setTjansteKontrakt(ab.getTjanstekontraktNamnrymd());
			abType.setReceiverId(ab.getLogiskAdressHsaId());

			abType.setSenderId(ab.getHsaIdTjanstekomponent());

			AnropsBehorighetsInfoIdType abId = new AnropsBehorighetsInfoIdType();
			abId.setValue(String.valueOf(ab.getIdAnropsbehorighet()));
			abType.setAnropsBehorighetsInfoId(abId);
			abType.setFromTidpunkt(XmlGregorianCalendarUtil.fromDate(ab.getFromTidpunkt()));
			abType.setTomTidpunkt(XmlGregorianCalendarUtil.fromDate(ab.getTomTidpunkt()));

			if(ab.getFilterInfos() != null && !ab.getFilterInfos().isEmpty()) {
				for(FilterInfo filterInfo : ab.getFilterInfos()) {
					FilterInfoType filterInfoType = new FilterInfoType();
					filterInfoType.setServiceDomain(filterInfo.getServicedomain());
					if(filterInfo.getFilterCategorizations() != null && !filterInfo.getFilterCategorizations().isEmpty()) {
						for(String categorization : filterInfo.getFilterCategorizations()) {
							filterInfoType.getCategorization().add(categorization);
						}
					}
					abType.getFilterInfo().add(filterInfoType);
				}
			}
			response.getAnropsBehorighetsInfo().add(abType);
		}
		
		log.info("Response returned from tk-admin-services hamtaAllaAnropsBehorigheter v2");
		return response;

	}

	/**
	 * Hamta en lista av alla virtualiseringar.
	 * 
	 * @param parameters
	 *            - null, eftersom operationen inte har nÃ¥gon payload.
	 */
	public HamtaAllaVirtualiseringarResponseType hamtaAllaVirtualiseringar(Object parameters) {
		
		log.info("Request to tk-admin-services hamtaAllaVirtualiseringar v2, p: {}", (parameters == null) ? null : parameters.getClass().getName());

		HamtaAllaVirtualiseringarResponseType response = new HamtaAllaVirtualiseringarResponseType();

        List<VirtualiseringInfo> virtualiseringar = takSyncService.getAllVagval();

		for (VirtualiseringInfo vi : virtualiseringar) {

			VirtualiseringsInfoType viType = new VirtualiseringsInfoType();

			viType.setTjansteKontrakt(vi.getNamnrymd());
			viType.setReceiverId(vi.getHsaIdLogiskAddresat());

			VirtualiseringsInfoIdType viId = new VirtualiseringsInfoIdType();
			viId.setValue(String.valueOf(vi.getIdLogiskAdress()));
			viType.setVirtualiseringsInfoId(viId);
			viType.setFromTidpunkt(XmlGregorianCalendarUtil.fromDate(vi.getFromTidpunkt()));
			viType.setTomTidpunkt(XmlGregorianCalendarUtil.fromDate(vi.getTomTidpunkt()));

			viType.setAdress(vi.getAdress());
			viType.setRivProfil(vi.getNamnRiv());

			response.getVirtualiseringsInfo().add(viType);
		}
		
		log.info("Response returned from tk-admin-services hamtaAllaVirtualiseringar v2");
		return response;

	}

	@Override
	public HamtaAllaTjanstekomponenterResponseType hamtaAllaTjanstekomponenter(
			Object parameters) {
		
		log.info("Request to tk-admin-services hamtaAllaTjanstekomponenter v2, p: {}", (parameters == null) ? null : parameters.getClass().getName());
		
		HamtaAllaTjanstekomponenterResponseType response = new HamtaAllaTjanstekomponenterResponseType();
		
		List<TjanstekomponentInfo> tjanstekomponenter = takSyncService.getAllTjanstekomponent();
		for (TjanstekomponentInfo tk: tjanstekomponenter) {
			// Tjanstekomponent
			TjanstekomponentInfoType tkType = new TjanstekomponentInfoType();
			tkType.setBeskrivning(tk.getBeskrivning());
			tkType.setHsaId(tk.getHsaId());
			response.getTjanstekomponentInfo().add(tkType);
			
			// Anropsadress
			for (AnropsAdressInfo aai : tk.getAnropsAdressInfos()) {
				AnropsAdressInfoType aaiType = new AnropsAdressInfoType();
				aaiType.setAdress(aai.getAdress());
				aaiType.setRivtaProfilNamn(aai.getRivtaProfilNamn());
				tkType.getAnropsAdressInfo().add(aaiType);
				
				// Vagval
				for (VagvalsInfo vv : aai.getVagvalsInfos()) {
					VagvalsInfoType vvType = new VagvalsInfoType();
					vvType.setFromTidpunkt(XmlGregorianCalendarUtil.fromDate(vv.getFromTidpunkt()));
					vvType.setTomTidpunkt(XmlGregorianCalendarUtil.fromDate(vv.getTomTidpunkt()));
					vvType.setLogiskAdressBeskrivning(vv.getLogiskAdressBeskrivning());
					vvType.setLogiskAdressHsaId(vv.getLogiskAdressHsaId());
					vvType.setTjanstekontraktNamnrymd(vv.getTjanstekontraktNamnrymd());
					aaiType.getVagvalsInfo().add(vvType);
				}
			}
			
			// Anropsbehorighet
			for (AnropsbehorighetInfo abi : tk.getAnropsbehorighetInfos()) {
				AnropsbehorighetInfoForTjanstekomponentType abiType = new AnropsbehorighetInfoForTjanstekomponentType();
				abiType.setFromTidpunkt(XmlGregorianCalendarUtil.fromDate(abi.getFromTidpunkt()));
				abiType.setTomTidpunkt(XmlGregorianCalendarUtil.fromDate(abi.getTomTidpunkt()));
				abiType.setIntegrationsavtal(abi.getIntegrationsavtal());
				abiType.setLogiskAdressBeskrivning(abi.getLogiskAdressBeskrivning());
				abiType.setLogiskAdressHsaId(abi.getLogiskAdressHsaId());
				abiType.setTjanstekontraktNamnrymd(abi.getTjanstekontraktNamnrymd());
				tkType.getAnropsbehorighetInfo().add(abiType);
			}
			
		}
		
		log.info("Response returned from tk-admin-services hamtaAllaTjanstekomponenter v2");
		return response;
	}
}
