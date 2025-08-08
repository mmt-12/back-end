package com.memento.server.api.service.emoji.dto.response;

import com.memento.server.domain.community.Associate;

import lombok.Builder;

@Builder
public record EmojiAuthorResponse(
	Long id,
	String nickname,
	String imageUrl
) {

	public static EmojiAuthorResponse of(Associate associate) {
		return EmojiAuthorResponse.builder()
			.id(associate.getId())
			.nickname(associate.getNickname())
			.imageUrl(associate.getProfileImageUrl())
			.build();
	}
}
