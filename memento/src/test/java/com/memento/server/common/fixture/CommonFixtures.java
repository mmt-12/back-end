package com.memento.server.common.fixture;

import static com.memento.server.config.MinioProperties.*;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.util.UUID;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memento.server.config.MinioProperties;

public class CommonFixtures {

	private static final String VOICE_NAME = "voice";
	private static final String VOICE_FILE_NAME = "test.wav";
	private static final String VOICE_CONTENT_TYPE = "audio/wav";
	private static final byte[] VOICE_CONTENT = "test voice content".getBytes();

	private static final String IMAGE_NAME = "emoji";
	private static final String IMAGE_FILE_NAME = "test.png";
	private static final String IMAGE_CONTENT_TYPE = "image/png";
	private static final byte[] IMAGE_CONTENT = "test image content".getBytes();

	private static final String JSON_NAME = "data";
	private static final String JSON_FILE_NAME = "request";
	private static final String JSON_CONTENT_TYPE = "application/json";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static MockMultipartFile voiceFile() {
		return new MockMultipartFile(VOICE_NAME, VOICE_FILE_NAME, VOICE_CONTENT_TYPE, VOICE_CONTENT);
	}

	public static MockMultipartFile emojiFile() {
		return new MockMultipartFile(IMAGE_NAME, IMAGE_FILE_NAME, IMAGE_CONTENT_TYPE, IMAGE_CONTENT);
	}

	public static MockMultipartFile jsonFile(Object object) throws Exception {
		return new MockMultipartFile(JSON_NAME, JSON_FILE_NAME, JSON_CONTENT_TYPE,
			objectMapper.writeValueAsString(object).getBytes());
	}

	public static String mockUrl(MinioProperties minioProperties, MultipartFile file, FileType fileType) {
		return minioProperties.getUrl() + "/" + minioProperties.getBucketName(fileType) + "/" + UUID.randomUUID()
			+ "." + getExtension(file.getOriginalFilename());
	}
}
