package com.memento.server.api.service.minio;

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.config.MinioConfig;
import com.memento.server.config.MinioProperties;

import com.memento.server.common.exception.MementoException;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;

import static com.memento.server.common.error.ErrorCodes.MINIO_EXCEPTION;

@RequiredArgsConstructor
@Service
public class MinioService {

	private final MinioClient minioClient;
	private final MinioProperties minioProperties;

	public String createPermanentVoice(MultipartFile voice) {
		try {
			String extension = getExtension(voice.getOriginalFilename());
			String filename = UUID.randomUUID() + "." + extension;
			long contentLength = voice.getBytes().length;

			try (InputStream inputStream = voice.getInputStream()) {
				minioClient.putObject(
					PutObjectArgs.builder()
						.bucket(minioProperties.getBucket())
						.object(filename)
						.stream(inputStream, contentLength, -1)
						.contentType(voice.getContentType())
						.build()
				);
			}

			return minioProperties.getUrl() + "/" + minioProperties.getBucket() + "/" + filename;
		} catch (Exception e) {
			throw new MementoException(MINIO_EXCEPTION);
		}
	}
}
