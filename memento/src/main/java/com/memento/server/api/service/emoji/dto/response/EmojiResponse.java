package com.memento.server.api.service.emoji.dto.response;

import com.memento.server.domain.emoji.Emoji;

import lombok.Builder;

@Builder
public record EmojiResponse(
	Long id,
	String name,
	String url,
	EmojiAuthorResponse author
) {

	public static EmojiResponse of(Emoji emoji) {
		return EmojiResponse.builder()
			.id(emoji.getId())
			.name(emoji.getName())
			.url(emoji.getUrl())
			.author(EmojiAuthorResponse.of(emoji.getAssociate()))
			.build();
	}
}
