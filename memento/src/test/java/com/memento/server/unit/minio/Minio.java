package com.memento.server.unit.minio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;

public class Minio {

	private static final String BUCKET = "memento-local-bucket";
	private static final String URL = "http://localhost:9000";
	private static final String ACCESS_KEY = "minioadmin";
	private static final String SECRET_KEY = "minioadmin";

	private final MinioClient minioClient = MinioClient.builder()
		.endpoint(URL)
		.credentials(ACCESS_KEY, SECRET_KEY)
		.build();

	@Test
	@DisplayName("이미지 업로드 테스트")
	void uploadImage() throws Exception {
		// given
		ClassPathResource imageResource = getTestImageResource();
		String extension = getExtension(imageResource.getFilename());
		String filename = UUID.randomUUID() + "." + extension;
		long contentLength = imageResource.contentLength();
		String expectedContentType = "image/" + extension;

		// when
		try (InputStream inputStream = imageResource.getInputStream()) {
			minioClient.putObject(
				PutObjectArgs.builder()
					.bucket(BUCKET)
					.object(filename)
					.stream(inputStream, contentLength, -1)
					.contentType(expectedContentType)
					.build()
			);
		}

		// then
		StatObjectResponse stat = minioClient.statObject(
			StatObjectArgs.builder()
				.bucket(BUCKET)
				.object(filename)
				.build()
		);

		assertThat(stat).isNotNull();
		assertThat(stat.contentType()).isEqualTo(expectedContentType);
		assertThat(stat.size()).isEqualTo(contentLength);
	}

	@Test
	@DisplayName("이미지 조회 테스트")
	void readImage() throws Exception {
		// given
		ClassPathResource imageResource = getTestImageResource();
		String filename = uploadTestImage(imageResource);
		long contentLength = imageResource.contentLength();

		// when
		byte[] data;
		try (InputStream inputStream = minioClient.getObject(
			GetObjectArgs.builder()
				.bucket(BUCKET)
				.object(filename)
				.build()
		)) {
			data = inputStream.readAllBytes();
		}

		// then
		assertThat(data).isNotNull();
		assertThat(data).isNotEmpty();
		assertThat(data.length).isEqualTo(contentLength);
	}

	@Test
	@DisplayName("이미지 삭제 테스트")
	void deleteImage() throws Exception {
		// given
		ClassPathResource imageResource = getTestImageResource();
		String filename = uploadTestImage(imageResource);

		// when
		minioClient.removeObject(
			RemoveObjectArgs.builder()
				.bucket(BUCKET)
				.object(filename)
				.build()
		);

		// then
		assertThatThrownBy(() -> minioClient.statObject(
			StatObjectArgs.builder()
				.bucket(BUCKET)
				.object(filename)
				.build()
		)).isInstanceOf(ErrorResponseException.class)
			.hasMessageContaining("Object does not exist");
	}

	private String uploadTestImage(ClassPathResource imageResource) throws Exception {
		String extension = getExtension(imageResource.getFilename());
		String filename = UUID.randomUUID() + "." + extension;

		try (InputStream inputStream = imageResource.getInputStream()) {
			minioClient.putObject(
				PutObjectArgs.builder()
					.bucket(BUCKET)
					.object(filename)
					.stream(inputStream, imageResource.contentLength(), -1)
					.contentType("image/" + extension)
					.build()
			);
		}
		return filename;
	}

	private ClassPathResource getTestImageResource() {
		return new ClassPathResource("static/test-images/ooh.png");
	}

	private String getExtension(String filename) {
		return filename.substring(filename.lastIndexOf('.') + 1);
	}
}
