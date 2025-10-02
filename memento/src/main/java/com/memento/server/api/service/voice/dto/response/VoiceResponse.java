package com.memento.server.api.service.voice.dto.response;

import com.memento.server.domain.voice.Voice;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

@Builder
public record VoiceResponse(
	Long id,
	String name,
	String url,
	VoiceAuthorResponse author
) {

	public static VoiceResponse of(Voice voice) {
		return VoiceResponse.builder()
			.id(voice.getId())
			.name(voice.getName())
			.url(voice.getUrl())
			.author(VoiceAuthorResponse.of(voice.getAssociate()))
			.build();
	}

	@QueryProjection
	public VoiceResponse{

	}
}
