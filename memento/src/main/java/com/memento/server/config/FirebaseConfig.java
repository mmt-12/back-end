package com.memento.server.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {
	public FirebaseConfig() throws Exception {
		if (FirebaseApp.getApps().isEmpty()) {
			String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
			InputStream is;
			if (credPath != null && Files.exists(Path.of(credPath))) {
				is = Files.newInputStream(Path.of(credPath));
			} else {
				is = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
				if (is == null) {
					throw new IllegalStateException("FCM credentials not found. Set GOOGLE_APPLICATION_CREDENTIALS or add classpath:serviceAccountKey.json");
				}
			}

			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(is))
				.build();
			FirebaseApp.initializeApp(options);
		}
	}
}