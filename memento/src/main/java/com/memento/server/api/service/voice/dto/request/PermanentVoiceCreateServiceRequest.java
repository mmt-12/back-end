package com.memento.server.api.service.voice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record PermanentVoiceCreateServiceRequest(
	String name,
	Long associateId,
	MultipartFile voice
) {

	public static PermanentVoiceCreateServiceRequest of(String name, Long associateId, MultipartFile voice) {
		return PermanentVoiceCreateServiceRequest.builder()
				.name(name)
				.associateId(associateId)
				.voice(voice)
				.build();
	}
}
