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
