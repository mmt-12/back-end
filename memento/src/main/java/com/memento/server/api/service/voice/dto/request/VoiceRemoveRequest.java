package com.memento.server.api.service.voice.dto.request;

import lombok.Builder;

@Builder
public record VoiceRemoveRequest(
	Long communityId,
	Long voiceId
) {

	public static VoiceRemoveRequest of(Long communityId, Long voiceId) {
		return VoiceRemoveRequest.builder().communityId(communityId).voiceId(voiceId).build();
	}
}
