package com.demo.loan.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.demo.loan.management.model")
@EnableJpaRepositories("com.demo.loan.management.repository")
public class LoanManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(LoanManagementApplication.class, args);
	}
}
