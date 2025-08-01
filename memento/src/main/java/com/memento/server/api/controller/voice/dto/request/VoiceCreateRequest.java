package com.memento.server.api.controller.voice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.voice.dto.request.VoiceCreateServiceRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record VoiceCreateRequest(
	@NotBlank(message = "name 값은 필수입니다.")
	@Size(max = 34, message = "name은 최대 34자(한글 기준)까지 입력 가능합니다.")
	String name
) {
	public VoiceCreateServiceRequest toServiceRequest(MultipartFile voice) {
		return VoiceCreateServiceRequest.builder()
			.name(name)
			.voice(voice)
			.build();
	}
}
