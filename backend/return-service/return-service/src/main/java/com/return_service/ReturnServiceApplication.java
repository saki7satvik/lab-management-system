package com.return_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ReturnServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReturnServiceApplication.class, args);
	}

}
