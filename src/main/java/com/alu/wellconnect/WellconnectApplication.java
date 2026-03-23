package com.alu.wellconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WellconnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(WellconnectApplication.class, args);
	}

}
