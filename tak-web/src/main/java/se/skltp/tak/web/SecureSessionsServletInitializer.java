package se.skltp.tak.web;

import javax.servlet.ServletContext;
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
