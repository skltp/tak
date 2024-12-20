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


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.response.ResetCacheResponse;

@Path("/")
public class ResetPVCacheRESTService {
	private static final Logger log = LoggerFactory.getLogger(ResetPVCacheRESTService.class);

	private final TakPublishVersion takPublishVersion;

	private final TakSyncService takSyncService;

	public ResetPVCacheRESTService(TakSyncService takSyncService, TakPublishVersion takPublishVersion) {
		this.takSyncService = takSyncService;
		this.takPublishVersion = takPublishVersion;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResetCacheResponse resetPVCache(@QueryParam("version") Integer version) {
		log.info("Before reset");
		if (takPublishVersion == null) {
			log.error("Null takPublishVersion");
		}
		
		ResetCacheResponse resetCacheResp;
		try {
			resetCacheResp = resetCacheAndTestRunAllServices(new ResetCacheResponse(), version);
		} catch (Exception e) {
			resetCacheResp = new ResetCacheResponse();
			String msg = e.getMessage();
			log.error(msg, e);
			resetCacheResp.setMessage(msg);
			resetCacheResp.setStatus(ResetCacheResponse.STATUS.ERROR);
			log.info("Try rollback from TAK WEB");
		}
		
		log.info("Reset performed");
		return resetCacheResp;
	}
	
	private ResetCacheResponse resetCacheAndTestRunAllServices(ResetCacheResponse resetCacheResp, Integer version) {
		takPublishVersion.resetPVCache(version);

		resetCacheResp.setServicesList(ResetCacheResponse.SERVICES.VIRTUALISERING, takSyncService.getAllVagvalSize());

		resetCacheResp.setServicesList(ResetCacheResponse.SERVICES.ANROPSBEHORIGHT, takSyncService.getAllAnropsbehorighetAndFilterSize());

		resetCacheResp.setServicesList(ResetCacheResponse.SERVICES.TJANSTEKOMPONENT, takSyncService.getAllTjanstekomponentSize());
		
		resetCacheResp.setServicesList(ResetCacheResponse.SERVICES.TJANSTEKONTRAKT, takSyncService.getAllTjanstekontraktSize());
		
		resetCacheResp.setCurrentVersion(takPublishVersion.getCurrentVersion());
		resetCacheResp.setMessage("Successfully updated server with new published version");
		resetCacheResp.setStatus(ResetCacheResponse.STATUS.OK);
		
		return resetCacheResp;
	}
}
