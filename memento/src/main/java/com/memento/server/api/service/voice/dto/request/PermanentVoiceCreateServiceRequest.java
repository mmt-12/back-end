package com.memento.server.api.service.voice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record PermanentVoiceCreateServiceRequest(
	String name,
	Long associateId,
	MultipartFile voice
) {
}
