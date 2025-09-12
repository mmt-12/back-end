package com.memento.server.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {
	@PostConstruct
	public void init() throws Exception {
		String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
		InputStream in;

		if (credPath == null || credPath.isBlank()) {
			ClassPathResource r = new ClassPathResource("memento-serviceAccountKey.json");
			if (!r.exists()) throw new FileNotFoundException("classpath memento-serviceAccountKey.json not found");
			in = r.getInputStream();
		} else {
			File f = new File(credPath);
			if (f.isDirectory()) {
				throw new IOException("GOOGLE_APPLICATION_CREDENTIALS points to a directory: " + f);
			}
			if (!f.isFile()) throw new FileNotFoundException("File not found: " + f);
			in = new FileInputStream(f);
		}

		GoogleCredentials creds = GoogleCredentials.fromStream(in);
		FirebaseOptions opts = FirebaseOptions.builder().setCredentials(creds).build();
		if (FirebaseApp.getApps().isEmpty()) FirebaseApp.initializeApp(opts);
	}
}