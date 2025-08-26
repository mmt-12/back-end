package com.memento.server.spring.api.service.minio;

import static com.memento.server.common.error.ErrorCodes.MINIO_EXCEPTION;
import static com.memento.server.config.MinioProperties.FileType.POST;
import static com.memento.server.config.MinioProperties.FileType.VOICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.minio.MinioService;
import com.memento.server.common.exception.MementoException;
import com.memento.server.config.MinioProperties;
import com.memento.server.config.MinioProperties.FileType;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

@ExtendWith(MockitoExtension.class)
public class MinioServiceTest {

	@Mock
	private MinioClient minioClient;
	
	@Mock
	private MinioProperties minioProperties;
	
	@InjectMocks
	private MinioService minioService;

	@Test
	@DisplayName("파일을 업로드하고 URL을 반환한다.")
	void createFile() throws Exception {
		// given
		MultipartFile file = new MockMultipartFile("test", "test.jpg", "image/jpeg", "test content".getBytes());
		FileType fileType = POST;
		String bucketName = "post-bucket";
		String minioUrl = "http://localhost:9000";

		given(minioProperties.getBucketName(fileType)).willReturn(bucketName);
		given(minioProperties.getUrl()).willReturn(minioUrl);

		// when
		String result = minioService.createFile(file, fileType);

		// then
		assertThat(result).startsWith(minioUrl + "/" + bucketName + "/");
		assertThat(result).endsWith(".jpg");
		verify(minioClient).putObject(any(PutObjectArgs.class));
	}

	@Test
	@DisplayName("파일 업로드 중 예외 발생 시 MementoException을 던진다.")
	void createFileThrowsException() throws Exception {
		// given
		MultipartFile file = new MockMultipartFile("test", "test.jpg", "image/jpeg", "test content".getBytes());
		FileType fileType = VOICE;
		String bucketName = "voice-bucket";
		
		given(minioProperties.getBucketName(fileType)).willReturn(bucketName);
		doThrow(new RuntimeException("MinIO error")).when(minioClient).putObject(any(PutObjectArgs.class));
		
		// when & then
		assertThatThrownBy(() -> minioService.createFile(file, fileType))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(MINIO_EXCEPTION);
	}

	@Test
	@DisplayName("파일을 삭제한다.")
	void removeFile() throws Exception {
		// given
		String minioUrl = "http://localhost:9000";
		String bucketName = "post-bucket";
		String filename = "test-file.jpg";
		String url = minioUrl + "/" + bucketName + "/" + filename;
		
		given(minioProperties.getUrl()).willReturn(minioUrl);
		
		// when
		minioService.removeFile(url);
		
		// then
		verify(minioClient).removeObject(any(RemoveObjectArgs.class));
	}

	@Test
	@DisplayName("파일 삭제 중 예외 발생 시 MementoException을 던진다.")
	void removeFileThrowsException() throws Exception {
		// given
		String minioUrl = "http://localhost:9000";
		String bucketName = "voice-bucket";
		String filename = "test-voice.wav";
		String url = minioUrl + "/" + bucketName + "/" + filename;
		
		given(minioProperties.getUrl()).willReturn(minioUrl);
		doThrow(new RuntimeException("MinIO error")).when(minioClient).removeObject(any(RemoveObjectArgs.class));
		
		// when & then
		assertThatThrownBy(() -> minioService.removeFile(url))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(MINIO_EXCEPTION);
	}
}