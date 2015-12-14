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
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.skltp.tak.core.facade.TakPublishVersion;

@Path("/")
public class ResetPVCacheRESTService {
	private static final Logger log = LoggerFactory.getLogger(ResetPVCacheRESTService.class);
	
	private TakPublishVersion takPublishVersion;
	
	public void setTakPublishVersion(final TakPublishVersion takPublishVersion) {
		this.takPublishVersion = takPublishVersion;
	}

	public ResetPVCacheRESTService() {}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/PV")
	public Response resetPVCache() {
		log.debug("Before reset");
		if (takPublishVersion == null) {
			log.error("Null takPublishVersion");
		}
		
		takPublishVersion.resetPVCache();
		return Response.ok("OK").build();
	}
}
