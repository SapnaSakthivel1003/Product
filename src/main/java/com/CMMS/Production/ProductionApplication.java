package com.CMMS.Production;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients
@EntityScan(basePackages = {
		"com.CMMS.Production.Entity",
		"com.CMMS.Master.Data.Entity"
})
@EnableJpaRepositories(basePackages = {
		"com.CMMS.Production.Repository",
		"com.CMMS.Master.Data.Repository"
})
public class ProductionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductionApplication.class, args);
	}

}
