/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web;

import jakarta.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("setSecureSessions")
public class SecureSessionsServletInitializer implements ServletContextInitializer {
  static final Logger log = LoggerFactory.getLogger(SecureSessionsServletInitializer.class);

  @Override
  public void onStartup(ServletContext servletContext) {
    servletContext.getSessionCookieConfig().setSecure(true);
    log.info("Session cookies are now secure (HTTPS required)");
  }
}
