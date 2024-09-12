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

import static org.junit.Assert.*;

import jakarta.ws.rs.core.Response;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import se.skltp.tak.response.GetStatusResponse;

public class GetApplicationStatusTest extends AbstractServiceTest {

	@Autowired
	GetApplicationStatus statusService;

	@Test
	public void testGetApplicationStatus() throws Exception {
		GetStatusResponse applicationStatus = statusService.getApplicationStatus();

		assertNotNull(applicationStatus);
		assertEquals(10, applicationStatus.getAppInfoList().size());
	}

	@Test
	public void testGetReadinessStatus() throws Exception {
		Response readiness = statusService.getReadinessStatus();

		assertNotNull(readiness);
		assertEquals(200, readiness.getStatus());
		assertEquals("OK", readiness.readEntity(String.class));
	}

	@Test
	public void testGetLivenessStatus() throws Exception {
		Response liveness = statusService.getLivenessStatus();

		assertNotNull(liveness);
		assertEquals(200, liveness.getStatus());
		assertEquals("OK", liveness.readEntity(String.class));
	}
}
