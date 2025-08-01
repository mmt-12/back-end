package com.memento.server.api.service.voice.dto.request;

import lombok.Builder;

@Builder
public record VoiceRemoveRequest(
	Long groupId,
	Long voiceId
) {

	public static VoiceRemoveRequest of(Long groupId, Long voiceId) {
		return VoiceRemoveRequest.builder().groupId(groupId).voiceId(voiceId).build();
	}
}
