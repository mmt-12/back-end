package com.memento.server.config;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {
	
	private Voice voice = new Voice();
	private Image image = new Image();
	private Post post = new Post();
	
	@Getter
	@Setter
	public static class Voice {
		private long maxSizeBytes;
		private Set<String> allowedTypes;
	}
	
	@Getter
	@Setter
	public static class Image {
		private long maxSizeBytes;
		private Set<String> allowedTypes;
	}
	
	@Getter
	@Setter
	public static class Post {
		private long maxTotalSizeBytes;
		private int maxFileCount;
	}
}