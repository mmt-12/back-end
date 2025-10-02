package com.memento.server.api.service.voice.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record VoiceListResponse(
	List<VoiceResponse> voices,
	Long nextCursor,
	boolean hasNext
) {

	public static VoiceListResponse of(List<VoiceResponse> voices, Long nextCursor, boolean hasNext) {
		return VoiceListResponse.builder()
			.voices(voices)
			.nextCursor(nextCursor)
			.hasNext(hasNext)
			.build();
	}
}
