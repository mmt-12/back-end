package com.memento.server.api.service.voice.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record VoiceCreateServiceRequest(
	String name,
	MultipartFile voice
) {
}