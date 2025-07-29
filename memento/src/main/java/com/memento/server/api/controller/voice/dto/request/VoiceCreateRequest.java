package com.memento.server.api.controller.voice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VoiceCreateRequest(
	@NotBlank
	String name,
	@NotNull
	MultipartFile voice
) {
}
