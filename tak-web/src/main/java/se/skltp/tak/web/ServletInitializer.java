package se.skltp.tak.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.stereotype.Component;

@Component
public class ServletInitializer extends SpringBootServletInitializer {
  static final Logger log = LoggerFactory.getLogger(ServletInitializer.class);

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(TakWebApplication.class);
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    if (isRunningInExternalTomcat(servletContext)) {
      servletContext.getSessionCookieConfig().setSecure(false);
      log.error("Running in external Tomcat, secure=false");
    } else {
      log.error("Running in embedded container, no changes to secure");
      servletContext.getSessionCookieConfig().setSecure(true);
    }
    super.onStartup(servletContext);
  }

  private boolean isRunningInExternalTomcat(ServletContext servletContext) {
    log.error("..........." + servletContext.getServerInfo());
    return servletContext.getServerInfo().contains("Tomcat") &&
        !servletContext.getServerInfo().contains("Spring Boot");
  }
}
