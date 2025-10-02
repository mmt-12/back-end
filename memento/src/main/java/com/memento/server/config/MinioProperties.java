package com.memento.server.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
	private String url;
	private String publicUrl;
	private String accessKey;
	private String secretKey;
	private Map<String, String> buckets;

	public enum FileType {
		POST("post"), 
		VOICE("voice"), 
		EMOJI("emoji"), 
		PROFILE_IMAGE("profile-image");

		private final String bucketKey;

		FileType(String bucketKey) {
			this.bucketKey = bucketKey;
		}

		public String getBucketKey() {
			return bucketKey;
		}
	}

	public String getBucketName(FileType fileType) {
		return buckets.get(fileType.getBucketKey());
	}
}
