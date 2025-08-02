package com.memento.server.api.service.emoji.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record EmojiCreateServiceRequest(
	String name,
	MultipartFile emoji
) {
}
