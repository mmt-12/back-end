package com.memento.server.api.service.voice.dto.request;

public record VoiceListQueryRequest(
	Long communityId,
	Long cursor,
	int size,
	String keyword
) {
	public static VoiceListQueryRequest of(Long communityId, Long cursor, int size, String keyword) {
		return new VoiceListQueryRequest(
			communityId,
			cursor,
			size,
			keyword
		);
	}
}