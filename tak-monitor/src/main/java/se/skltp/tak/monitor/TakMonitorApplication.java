package se.skltp.tak.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("se.skltp.tak.*")
@EntityScan("se.skltp.tak.*")
public class TakMonitorApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TakMonitorApplication.class, args);
	}

}
