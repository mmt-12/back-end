package com.memento.server.api.service.voice.dto.request;

public record VoiceListQueryRequest(
	Long groupId,
	Long cursor,
	int size,
	String keyword
) {
	public static VoiceListQueryRequest of(Long groupId, Long cursor, int size, String keyword) {
		return new VoiceListQueryRequest(
			groupId,
			cursor,
			size <= 0 ? 10 : size,
			keyword != null && !keyword.isBlank() ? keyword.trim() : null
		);
	}
}