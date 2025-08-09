package com.memento.server.api.service.voice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record TemporaryVoiceCreateServiceRequest(
	Long associateId,
	MultipartFile voice
) {

	public static TemporaryVoiceCreateServiceRequest of(Long associateId, MultipartFile voice) {
		return TemporaryVoiceCreateServiceRequest.builder()
			.associateId(associateId)
			.voice(voice)
			.build();
	}
}
