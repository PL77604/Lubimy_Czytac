package com.lubimyczytac.LubimyCzytac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class LubimyCzytacApplication {

	public static void main(String[] args) {
		SpringApplication.run(LubimyCzytacApplication.class, args);
	}
}
