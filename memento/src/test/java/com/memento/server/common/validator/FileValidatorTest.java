package com.memento.server.common.validator;

import static com.memento.server.common.error.ErrorCodes.IMAGE_FILE_TOO_LARGE;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.common.exception.MementoException;

@SpringBootTest
class FileValidatorTest {

	@Autowired
	private FileValidator fileValidator;

	@Test
	@DisplayName("올바른 음성 파일은 검증을 통과한다")
	void validateVoiceFile_Success() {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"voice", "test.wav", "audio/wav", "content".getBytes()
		);

		// when & then
		assertThatCode(() -> fileValidator.validateVoiceFile(file))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("음성 파일 크기가 제한을 초과하면 예외가 발생한다")
	void validateVoiceFile_FileTooLarge() {
		// given - 10MB + 1바이트로 제한 초과 테스트
		byte[] largeContent = new byte[25 * 1024 * 1024 + 1]; // 25MB + 1바이트
		MockMultipartFile file = new MockMultipartFile(
			"voice", "test.wav", "audio/wav", largeContent
		);

		// when & then
		assertThatThrownBy(() -> fileValidator.validateVoiceFile(file))
			.isInstanceOf(MementoException.class);
	}

	@Test
	@DisplayName("지원하지 않는 음성 파일 형식이면 예외가 발생한다")
	void validateVoiceFile_InvalidFormat() {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"voice", "test.txt", "text/plain", "content".getBytes()
		);

		// when & then
		assertThatThrownBy(() -> fileValidator.validateVoiceFile(file))
			.isInstanceOf(MementoException.class);
	}

	@Test
	@DisplayName("contentType이 null인 음성 파일이면 예외가 발생한다")
	void validateVoiceFile_NullContentType() {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"voice", "test.wav", null, "content".getBytes()
		);

		// when & then
		assertThatThrownBy(() -> fileValidator.validateVoiceFile(file))
			.isInstanceOf(MementoException.class);
	}

	@Test
	@DisplayName("올바른 이미지 파일은 검증을 통과한다")
	void validateImageFile_Success() {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"image", "test.jpg", "image/jpeg", "content".getBytes()
		);

		// when & then
		assertThatCode(() -> fileValidator.validateImageFile(file))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("이미지 파일 크기가 제한을 초과하면 예외가 발생한다")
	void validateImageFile_FileTooLarge() {
		// given - 5MB + 1바이트로 제한 초과 테스트
		byte[] largeContent = new byte[25 * 1024 * 1024 + 1]; // 25MB + 1바이트
		MockMultipartFile file = new MockMultipartFile(
			"image", "test.jpg", "image/jpeg", largeContent
		);

		// when & then
		assertThatThrownBy(() -> fileValidator.validateImageFile(file))
			.isInstanceOf(MementoException.class);
	}

	@Test
	@DisplayName("지원하지 않는 이미지 파일 형식이면 예외가 발생한다")
	void validateImageFile_InvalidFormat() {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"image", "test.txt", "text/plain", "content".getBytes()
		);

		// when & then
		assertThatThrownBy(() -> fileValidator.validateImageFile(file))
			.isInstanceOf(MementoException.class);
	}

	@Test
	@DisplayName("올바른 포스트 이미지들은 검증을 통과한다")
	void validatePostImages_Success() {
		// given
		MultipartFile[] files = {
			new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "content1".getBytes()),
			new MockMultipartFile("image2", "test2.png", "image/png", "content2".getBytes())
		};

		// when & then
		assertThatCode(() -> fileValidator.validatePostImages(files))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("포스트 파일 개수가 제한을 초과하면 예외가 발생한다")
	void validatePostImages_TooManyFiles() {
		// given - 11개 파일로 제한(10개) 초과 테스트
		MultipartFile[] files = new MultipartFile[26];
		for (int i = 0; i < 26; i++) {
			files[i] = new MockMultipartFile("image" + i, "test" + i + ".jpg", "image/jpeg", "content".getBytes());
		}

		// when & then
		assertThatThrownBy(() -> fileValidator.validatePostImages(files))
			.isInstanceOf(MementoException.class);
	}

	@Test
	@DisplayName("개별 포스트 이미지 파일 크기가 제한을 초과하면 예외가 발생한다")
	void validatePostImages_IndividualFileTooLarge() {
		// given - 개별 파일 크기 25MB 초과 테스트
		byte[] largeContent = new byte[26 * 1024 * 1024]; // 26MB (25MB 초과)
		MultipartFile[] files = {
			new MockMultipartFile("image1", "test1.jpg", "image/jpeg", largeContent)
		};

		// when & then
		assertThatThrownBy(() -> fileValidator.validatePostImages(files))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", IMAGE_FILE_TOO_LARGE);
	}

	@Test
	@DisplayName("null이나 빈 포스트 이미지 배열은 검증을 통과한다")
	void validatePostImages_NullOrEmpty() {
		// when & then
		assertThatCode(() -> fileValidator.validatePostImages(null))
			.doesNotThrowAnyException();
			
		assertThatCode(() -> fileValidator.validatePostImages(new MultipartFile[0]))
			.doesNotThrowAnyException();
	}
}