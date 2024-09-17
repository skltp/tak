package se.skltp.tak.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import se.skltp.tak.web.service.ConfigurationService;

import java.io.IOException;

@SpringBootApplication
@EntityScan("se.skltp.tak.*")
public class TakWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakWebApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(TakWebApplication.class);

	@Autowired
	ConfigurationService configurationService;

	@EventListener(ApplicationReadyEvent.class)
	public void addCustomConfiguration() {
		try {
			configurationService.init();
		}
		catch (IOException e) {
			log.error("Failed to load configuration: " + e.getMessage());
		}
	}
}
