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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.skltp.tak.core.facade.TakPublishVersion;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.response.GetStatusResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.catalina.util.ServerInfo;

import java.io.IOException;
import java.util.*;

@Path("/")
public class GetApplicationStatus {
	private static final Logger log = LoggerFactory.getLogger(GetApplicationStatus.class);
	private TakPublishVersion takPublishVersion;
	private TakSyncService takSyncService;

	public void setTakPublishVersion(final TakPublishVersion takPublishVersion) {
		this.takPublishVersion = takPublishVersion;
	}

	public void setTakSyncService(final TakSyncService takSyncService) {
		this.takSyncService = takSyncService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/status")
	public GetStatusResponse getApplicationStatus() {
		log.debug("Received status request");

		GetStatusResponse statusResp = new GetStatusResponse();

		final Properties properties = new Properties();
		try {
			log.debug("loading project.properties");
			properties.load(getClass().getClassLoader().getResourceAsStream("project.properties"));
		} catch (IOException e) {
			log.warn(e.toString());
			e.printStackTrace();
		}

		for (Object key : properties.keySet()) {
			statusResp.setAppInfoList((String) key, properties.getProperty((String) key));
		}

		String currentPublishVersion = takPublishVersion == null
				? "Not initialized"
				: String.format("%d", takPublishVersion.getCurrentVersion());

		statusResp.setAppInfoList("tak.pvCacheVersion", currentPublishVersion);
		statusResp.setAppInfoList("servletengine.info", ServerInfo.getServerInfo());
		statusResp.setAppInfoList("java.vendor", System.getProperty("java.vendor"));
		statusResp.setAppInfoList("java.version", System.getProperty("java.version"));
		statusResp.setAppInfoList("os.arch", System.getProperty("os.arch"));
		statusResp.setAppInfoList("os.name", System.getProperty("os.name"));
		statusResp.setAppInfoList("os.version", System.getProperty("os.version"));
		statusResp.setAppInfoList("user.name", System.getProperty("user.name"));

		return statusResp;
	}

	@GET
	@Path("/status/readiness")
	public Response getReadinessStatus() {
		log.debug("Received readiness request");
		try {
			// Smoke test for proper cache initialization
			takSyncService.getAllVagvalSize();
			return Response.ok("OK").build();
		}
		catch (Exception e) {
			log.warn("Readiness check failed: {}", e.getMessage());
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/status/liveness")
	public Response getLivenessStatus() {
		log.debug("Received liveness request");
		// Just a dummy response to indicate service is responsive
		return Response.ok("OK").build();
	}
}
