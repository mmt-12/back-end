package com.memento.server.api.service.emoji.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record EmojiCreateServiceRequest(
	String name,
	Long associateId,
	MultipartFile emoji
) {

	public static EmojiCreateServiceRequest of(String name, Long associateId, MultipartFile emoji) {
		return EmojiCreateServiceRequest.builder()
				.name(name)
				.associateId(associateId)
				.emoji(emoji)
				.build();
	}
}
