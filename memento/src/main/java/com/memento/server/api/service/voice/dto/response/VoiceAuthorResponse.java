package com.memento.server.api.service.voice.dto.response;

import com.memento.server.domain.community.Associate;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

@Builder
public record VoiceAuthorResponse(
	Long id,
	String nickname,
	String imageUrl
) {

	public static VoiceAuthorResponse of(Associate associate) {
		return VoiceAuthorResponse.builder()
			.id(associate.getId())
			.nickname(associate.getNickname())
			.imageUrl(associate.getProfileImageUrl())
			.build();
	}

	@QueryProjection
	public VoiceAuthorResponse {

	}
}
