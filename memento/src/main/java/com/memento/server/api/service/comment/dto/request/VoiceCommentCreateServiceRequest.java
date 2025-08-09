package com.memento.server.api.service.comment.dto.request;

import lombok.Builder;

@Builder
public record VoiceCommentCreateServiceRequest(
	Long voiceId,
	Long postId,
	Long associateId
) {

	public static VoiceCommentCreateServiceRequest of(Long voiceId, Long postId, Long associateId) {
		return VoiceCommentCreateServiceRequest.builder()
			.voiceId(voiceId)
			.postId(postId)
			.associateId(associateId)
			.build();
	}
}
