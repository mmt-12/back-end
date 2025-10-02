package com.memento.server.api.service.voice.dto.request;

import lombok.Builder;

@Builder
public record VoiceRemoveRequest(
	Long associateId,
	Long voiceId
) {

	public static VoiceRemoveRequest of(Long associateId, Long voiceId) {
		return VoiceRemoveRequest.builder().associateId(associateId).voiceId(voiceId).build();
	}
}
