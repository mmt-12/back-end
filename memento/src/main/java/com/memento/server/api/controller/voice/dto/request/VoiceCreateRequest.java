package com.memento.server.api.controller.voice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.voice.dto.request.VoiceCreateServiceRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VoiceCreateRequest(
	@NotBlank(message = "name 값은 필수입니다.")
	String name,
	@NotNull(message = "voice는 필수입니다.")
	MultipartFile voice
) {
	public VoiceCreateServiceRequest toServiceRequest() {
		return VoiceCreateServiceRequest.builder()
			.name(name)
			.voice(voice)
			.build();
	}
}
