package se.skltp.tak.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("se.skltp.tak.*")
public class TakWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakWebApplication.class, args);
	}

}
