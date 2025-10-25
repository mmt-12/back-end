package com.memento.server.api.service.emoji.dto.response;

import com.memento.server.domain.community.Associate;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

@Builder
public record EmojiAuthorResponse(
	Long id,
	String nickname,
	String imageUrl
) {

	public static EmojiAuthorResponse from(Associate associate) {
		return EmojiAuthorResponse.builder()
			.id(associate.getId())
			.nickname(associate.getNickname())
			.imageUrl(associate.getProfileImageUrl())
			.build();
	}

	@QueryProjection
	public EmojiAuthorResponse {

	}
}
