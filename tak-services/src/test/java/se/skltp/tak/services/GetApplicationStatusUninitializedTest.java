/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.services;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import se.skltp.tak.response.GetStatusResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetApplicationStatusUninitializedTest {

  GetApplicationStatus statusService = new GetApplicationStatus();

  @Test
  void testGetApplicationStatusUninitalized() {
    GetStatusResponse applicationStatus = statusService.getApplicationStatus();

    assertNotNull(applicationStatus);
    assertEquals(10, applicationStatus.getAppInfoList().size());
  }

  @Test
  void testGetReadinessStatusNotReady() {
    Response readiness = statusService.getReadinessStatus();

    assertNotNull(readiness);
    assertEquals(503, readiness.getStatus());
  }

  @Test
  void testGetLivenessStatusOkWhenUninitalized() {
    Response liveness = statusService.getLivenessStatus();

    assertNotNull(liveness);
    assertEquals(200, liveness.getStatus());
    assertEquals("OK", liveness.readEntity(String.class));
  }
}
