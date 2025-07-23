package com.example.CAPSTONE_BACKEND;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.example.CAPSTONE_BACKEND",
		"security",
		"auth",
		"controllers",
		"dtos",
		"entities",
		"enumerations",
		"repositories",
		"services",
		"specifications"
})
@EnableJpaRepositories(basePackages = "repositories")
@EntityScan(basePackages = "entities")
public class CapstoneBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(CapstoneBackendApplication.class, args);
	}
}