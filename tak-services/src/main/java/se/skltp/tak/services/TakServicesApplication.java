/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.SpringVersion;

@SpringBootApplication
@ComponentScan(basePackages = "se.skltp.tak")
@EntityScan("se.skltp.tak")
public class TakServicesApplication {
    private final static Logger log = LoggerFactory.getLogger(TakServicesApplication.class);

    public static void main(String[] args) {
        log.info("Application Launching with Spring Boot v{}, Spring v{}",
                SpringBootVersion.getVersion(),
                SpringVersion.getVersion());
        SpringApplication.run(TakServicesApplication.class, args);
    }
}
