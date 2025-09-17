package com.cartechindia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class cartechindiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(cartechindiaApplication.class, args);
	}
}
