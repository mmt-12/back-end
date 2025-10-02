package com.memento.server.api.service.minio;

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.config.MinioProperties;

import com.memento.server.common.exception.MementoException;
import com.memento.server.config.MinioProperties.FileType;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;

import static com.memento.server.common.error.ErrorCodes.MINIO_EXCEPTION;

@RequiredArgsConstructor
@Service
public class MinioService {

	private final MinioClient minioClient;
	private final MinioProperties minioProperties;

	public String createFile(MultipartFile file, FileType fileType) {
		try {
			String bucket = minioProperties.getBucketName(fileType);
			String extension = getExtension(file.getOriginalFilename());
			String filename = UUID.randomUUID() + "." + extension;
			long contentLength = file.getBytes().length;

			try (InputStream inputStream = file.getInputStream()) {
				minioClient.putObject(
					PutObjectArgs.builder()
						.bucket(bucket)
						.object(filename)
						.stream(inputStream, contentLength, -1)
						.contentType(file.getContentType())
						.build()
				);
			}

			return minioProperties.getPublicUrl() + "/" + bucket + "/" + filename;
		} catch (Exception e) {
			throw new MementoException(MINIO_EXCEPTION);
		}
	}

	public void removeFile(String url) {
		try {
			String baseUrl = minioProperties.getPublicUrl() + "/";
			String pathAfterUrl = url.substring(baseUrl.length());
			String[] parts = pathAfterUrl.split("/", 2);
			String bucket = parts[0];
			String filename = parts[1];

			minioClient.removeObject(
				RemoveObjectArgs.builder()
					.bucket(bucket)
					.object(filename)
					.build()
			);
		} catch (Exception e) {
			throw new MementoException(MINIO_EXCEPTION);
		}
	}
}
