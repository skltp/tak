package se.skltp.tak.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "se.skltp.tak")
@EntityScan("se.skltp.tak")
public class TakServicesApplication {
    public static void main(String[] args) {
        SpringApplication.run(TakServicesApplication.class, args);
    }
}
