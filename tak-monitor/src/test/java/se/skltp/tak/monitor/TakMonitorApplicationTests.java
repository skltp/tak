/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.monitor;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@SpringBootTest
class TakMonitorApplicationTests {

  @Test
  void contextLoads() {}

  /**
   * This Bean takes over CoreV1Api responsibilities during testing.
   */
  @SpringBootTest
  @TestConfiguration
  static class TestConfig {
    @Bean
    CoreV1Api coreV1Api() {
      return mock(CoreV1Api.class);
    }
  }
}
