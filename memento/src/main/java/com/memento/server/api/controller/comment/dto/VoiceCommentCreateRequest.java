package com.memento.server.api.controller.comment.dto;

import com.memento.server.api.service.comment.dto.request.VoiceCommentCreateServiceRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record VoiceCommentCreateRequest(
	@NotNull(message = "voiceId 값은 필수입니다.")
	Long voiceId
) {

	public VoiceCommentCreateServiceRequest toServiceRequest(Long postId, Long associateId) {
		return VoiceCommentCreateServiceRequest.builder()
			.voiceId(voiceId)
			.postId(postId)
			.associateId(associateId)
			.build();
	}
}
