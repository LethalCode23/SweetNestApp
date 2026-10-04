package com.dh.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SweetNestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SweetNestApplication.class, args);
	}
}