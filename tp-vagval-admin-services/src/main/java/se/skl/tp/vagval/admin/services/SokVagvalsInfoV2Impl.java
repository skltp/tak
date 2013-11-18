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
package se.skl.tp.vagval.admin.services;

import java.util.List;

import javax.jws.WebService;

import se.skl.tp.vagval.admin.core.facade.AnropsbehorighetInfo;
import se.skl.tp.vagval.admin.core.facade.FilterInfo;
import se.skl.tp.vagval.admin.core.facade.VagvalSyncService;
import se.skl.tp.vagval.admin.core.facade.VirtualiseringInfo;
import se.skltp.tk.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoIdType;
import se.skltp.tk.vagvalsinfo.wsdl.v2.AnropsBehorighetsInfoType;
import se.skltp.tk.vagvalsinfo.wsdl.v2.FilterInfoType;
import se.skltp.tk.vagvalsinfo.wsdl.v2.HamtaAllaAnropsBehorigheterResponseType;
import se.skltp.tk.vagvalsinfo.wsdl.v2.HamtaAllaVirtualiseringarResponseType;
import se.skltp.tk.vagvalsinfo.wsdl.v2.SokVagvalsInfoInterface;
import se.skltp.tk.vagvalsinfo.wsdl.v2.VirtualiseringsInfoIdType;
import se.skltp.tk.vagvalsinfo.wsdl.v2.VirtualiseringsInfoType;

@WebService(portName = "SokVagvalsSoap11LitDocPort", serviceName = "SokVagvalsServiceSoap11LitDocService", targetNamespace = "urn:skl:tp:vagvalsinfo:v2")
public class SokVagvalsInfoV2Impl implements SokVagvalsInfoInterface {

	VagvalSyncService vagvalSyncService;

	public void setVagvalSyncService(VagvalSyncService vagvalSyncService) {
		this.vagvalSyncService = vagvalSyncService;
	}

	/**
	 * Hamta en lista av alla anropsbehörigheter.
	 * 
	 * @param parameters
	 *            - null, eftersom operationen inte har någon payload.
	 */
	public HamtaAllaAnropsBehorigheterResponseType hamtaAllaAnropsBehorigheter(Object parameters) {

		HamtaAllaAnropsBehorigheterResponseType response = new HamtaAllaAnropsBehorigheterResponseType();

		List<AnropsbehorighetInfo> anropsbehorigheter = null;
		if (parameters != null) {
			String namnrymd = (String) parameters;
			anropsbehorigheter = vagvalSyncService.getAnropsbehorighetAndFilterByTjanstekontrakt(namnrymd);
		} else {
			anropsbehorigheter = vagvalSyncService.getAllAnropsbehorighet();
		}
		for (AnropsbehorighetInfo ab : anropsbehorigheter) {

			AnropsBehorighetsInfoType abType = new AnropsBehorighetsInfoType();

			abType.setTjansteKontrakt(ab.getNamnrymd());
			abType.setReceiverId(ab.getHsaIdLogiskAddresat());

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
		return response;

	}

	/**
	 * Hamta en lista av alla virtualiseringar.
	 * 
	 * @param parameters
	 *            - null, eftersom operationen inte har nÃ¥gon payload.
	 */
	public HamtaAllaVirtualiseringarResponseType hamtaAllaVirtualiseringar(Object parameters) {

		HamtaAllaVirtualiseringarResponseType response = new HamtaAllaVirtualiseringarResponseType();

		List<VirtualiseringInfo> virtualiseringar = null;
		if (parameters != null) {
			String namnrymd = (String) parameters;
			virtualiseringar = vagvalSyncService.getVirtualiseringByTjanstekontrakt(namnrymd);
		} else {
			virtualiseringar = vagvalSyncService.getAllVirtualisering();
		}

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
		return response;

	}
}
