package com.memento.server.api.service.comment.dto.request;

import lombok.Builder;

@Builder
public record EmojiCommentCreateServiceRequest(
	Long emojiId,
	Long postId,
	Long associateId
) {
}
