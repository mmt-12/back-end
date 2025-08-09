package com.memento.server.api.controller.comment.dto;

import com.memento.server.api.service.comment.dto.request.EmojiCommentCreateServiceRequest;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EmojiCommentCreateRequest(
	@NotNull(message = "emojiId 값은 필수입니다.")
	Long emojiId
) {

	public EmojiCommentCreateServiceRequest toServiceRequest(Long postId, Long associateId) {
		return EmojiCommentCreateServiceRequest.builder()
			.emojiId(emojiId)
			.postId(postId)
			.associateId(associateId)
			.build();
	}
}
