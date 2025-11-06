package com.memento.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MementoApplication {

	public static void main(String[] args) {
		System.setProperty("org.apache.tomcat.util.http.fileupload.fileCountMax", "20");
		System.out.println(System.getProperty("org.apache.tomcat.util.http.fileupload.fileCountMax"));
		SpringApplication.run(MementoApplication.class, args);
	}
}
