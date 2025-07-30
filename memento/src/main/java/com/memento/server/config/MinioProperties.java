package com.memento.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
	private String url;
	private String accessKey;
	private String secretKey;
	private String bucket;
}
