package se.skltp.tak.monitor;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@ComponentScan("se.skltp.tak.*")
@EntityScan("se.skltp.tak.*")
public class TakMonitorApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(TakMonitorApplication.class, args);
  }

  @Bean
  CoreV1Api coreV1Api() throws IOException {
    return new CoreV1Api(Config.defaultClient());
  }
}
