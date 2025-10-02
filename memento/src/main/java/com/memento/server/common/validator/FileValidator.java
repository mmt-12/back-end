package com.memento.server.common.validator;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.common.exception.MementoException;
import com.memento.server.config.FileUploadProperties;

import lombok.RequiredArgsConstructor;

import static com.memento.server.common.error.ErrorCodes.*;

@Component
@RequiredArgsConstructor
public class FileValidator {

	private final FileUploadProperties fileUploadProperties;

	public void validateVoiceFile(MultipartFile file) {
		FileUploadProperties.Voice config = fileUploadProperties.getVoice();
		validateFileSize(file, config.getMaxSizeBytes(), VOICE_FILE_TOO_LARGE);
		validateFileType(file, config.getAllowedTypes(), VOICE_INVALID_FORMAT);
	}

	public void validateImageFile(MultipartFile file) {
		FileUploadProperties.Image config = fileUploadProperties.getImage();
		validateFileSize(file, config.getMaxSizeBytes(), IMAGE_FILE_TOO_LARGE);
		validateFileType(file, config.getAllowedTypes(), IMAGE_INVALID_FORMAT);
	}

	public void validatePostImages(MultipartFile[] files) {
		if (files == null || files.length == 0) {
			return;
		}

		FileUploadProperties.Post config = fileUploadProperties.getPost();

		if (files.length > config.getMaxFileCount()) {
			throw new MementoException(POST_TOO_MANY_FILES);
		}

		for (MultipartFile file : files) {
			if (file != null && !file.isEmpty()) {
				validateImageFile(file);
			}
		}
	}

	private void validateFileSize(MultipartFile file, long maxSizeBytes, Object errorCode) {
		if (file.getSize() > maxSizeBytes) {
			throw new MementoException((com.memento.server.common.error.ErrorCode) errorCode);
		}
	}

	private void validateFileType(MultipartFile file, Set<String> allowedTypes, Object errorCode) {
		String contentType = file.getContentType();
		if (!isValidContentType(contentType, allowedTypes)) {
			throw new MementoException((com.memento.server.common.error.ErrorCode) errorCode);
		}
	}

	private boolean isValidContentType(String contentType, Set<String> allowedTypes) {
		if (contentType == null) {
			return false;
		}
		return allowedTypes.contains(contentType);
	}
}