package com.memento.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MementoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MementoApplication.class, args);
	}

}
