package com.mzc.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class SimpleBootApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SimpleBootApplication.class, args);
	}

	@GetMapping("/health")
	public String home() throws Exception{
		  return "ok";
	}
}


