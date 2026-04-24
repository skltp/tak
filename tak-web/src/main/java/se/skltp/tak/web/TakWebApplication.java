/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.event.EventListener;
import se.skltp.tak.web.service.ConfigurationService;

import java.io.IOException;

@SpringBootApplication(exclude = SessionAutoConfiguration.class) // Configuring this manually in JdbcConfig instead
@EnableFeignClients
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
