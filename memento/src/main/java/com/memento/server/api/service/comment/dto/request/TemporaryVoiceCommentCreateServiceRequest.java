package com.memento.server.api.service.comment.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record TemporaryVoiceCommentCreateServiceRequest(
	Long postId,
	Long associateId,
	MultipartFile voice
) {

	public static TemporaryVoiceCommentCreateServiceRequest of(Long postId, Long associateId, MultipartFile voice) {
		return TemporaryVoiceCommentCreateServiceRequest.builder()
			.postId(postId)
			.associateId(associateId)
			.voice(voice)
			.build();
	}
}
