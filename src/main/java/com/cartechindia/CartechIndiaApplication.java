package com.cartechindia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class CartechIndiaApplication {

	public static void main(String[] args) {
        SpringApplication.run(CartechIndiaApplication.class, args);
	}
}
