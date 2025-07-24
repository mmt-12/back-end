package com.memento.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioConfig {

	private final MinioClient minioClient;

	@Value("${minio.bucket}")
	private String bucketName;

	@PostConstruct
	public void initBucket() {
		try {
			boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
			if (!found) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
			}
		} catch (Exception e) {
			log.error("Failed to initialize MinIO bucket: {}", e.getMessage(), e);
		}
	}
}
