package com.example.its;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.its.domain")
public class ItsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItsApplication.class, args);
	}

}