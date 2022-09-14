package se.skltp.tak.web;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import se.skltp.tak.web.realm.ShiroDbRealm;

@SpringBootApplication
@EntityScan("se.skltp.tak.*")
public class TakWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(TakWebApplication.class, args);
	}

	@Bean
	public Realm customRealm() {
		return new ShiroDbRealm();
	}

	@Bean
	public ShiroFilterChainDefinition shiroFilterChainDefinition() {
		DefaultShiroFilterChainDefinition filter = new DefaultShiroFilterChainDefinition();

		//filter.addPathDefinition("/home", "authc");
		//filter.addPathDefinition("/**", "anon");

		return filter;
	}
}
