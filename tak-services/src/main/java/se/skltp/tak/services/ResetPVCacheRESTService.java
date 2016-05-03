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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.response.ResetCacheResponse;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaAnropsBehorigheterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaTjanstekomponenterResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaTjanstekontraktResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.HamtaAllaVirtualiseringarResponseType;
import se.skltp.tak.vagvalsinfo.wsdl.v2.SokVagvalsInfoInterface;

@Path("/")
public class ResetPVCacheRESTService {
	private static final Logger log = LoggerFactory.getLogger(ResetPVCacheRESTService.class);
	
	private TakPublishVersion takPublishVersion;
	
	public void setTakPublishVersion(final TakPublishVersion takPublishVersion) {
		this.takPublishVersion = takPublishVersion;
	}
	
	@Autowired
	private SokVagvalsInfoInterface sokVagvalsInfoInterface;

	public ResetPVCacheRESTService() {}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/pv")
	public ResetCacheResponse resetPVCache() {
		log.info("Before reset");
		if (takPublishVersion == null) {
			log.error("Null takPublishVersion");
		}
		
		ResetCacheResponse resetCacheResp = new ResetCacheResponse();
		try {
			resetCacheResp = resetCacheAndTestRunAllServices(resetCacheResp);
		} catch (Exception e) {
			String msg = "Something went wrong with the new published version, try rollback from TAK-WEB";
			log.error(msg + e.getMessage());
			resetCacheResp.setMessage(msg);
			resetCacheResp.setStatus(ResetCacheResponse.STATUS.ERROR);
			log.info("Try rollback from TAK WEB");
		}
		
		log.info("Reset performed");
		return resetCacheResp;
	}
	
	private ResetCacheResponse resetCacheAndTestRunAllServices(ResetCacheResponse resetCacheResp) {
		takPublishVersion.resetPVCache();
		
		HamtaAllaVirtualiseringarResponseType virtualiseringResp = sokVagvalsInfoInterface.hamtaAllaVirtualiseringar(null);
		resetCacheResp.setServicesList(ResetCacheResponse.SERVICES.VIRTUALISERING, virtualiseringResp.getVirtualiseringsInfo().size());
		
		HamtaAllaAnropsBehorigheterResponseType anropsbehorighetResp = sokVagvalsInfoInterface.hamtaAllaAnropsBehorigheter(null);
		resetCacheResp.setServicesList(ResetCacheResponse.SERVICES.ANROPSBEHORIGHT, anropsbehorighetResp.getAnropsBehorighetsInfo().size());

		HamtaAllaTjanstekomponenterResponseType tjanstekomponentResp = sokVagvalsInfoInterface.hamtaAllaTjanstekomponenter(null);
		resetCacheResp.setServicesList(ResetCacheResponse.SERVICES.TJANSTEKOMPONENT, tjanstekomponentResp.getTjanstekomponentInfo().size());
		
		HamtaAllaTjanstekontraktResponseType tjanstekontraktResp = sokVagvalsInfoInterface.hamtaAllaTjanstekontrakt(null);
		resetCacheResp.setServicesList(ResetCacheResponse.SERVICES.TJANSTEKONTRAKT, tjanstekontraktResp.getTjanstekontraktInfo().size());
		
		resetCacheResp.setCurrentVersion(takPublishVersion.getCurrentVersion());
		resetCacheResp.setMessage("Successfully updated server with new published version");
		resetCacheResp.setStatus(ResetCacheResponse.STATUS.OK);
		
		return resetCacheResp;
	}
}
