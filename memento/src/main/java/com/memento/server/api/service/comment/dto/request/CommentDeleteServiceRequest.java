package com.memento.server.api.service.comment.dto.request;

import lombok.Builder;

@Builder
public record CommentDeleteServiceRequest(
	Long commentId,
	Long associateId
) {

	public static CommentDeleteServiceRequest of(Long commentId, Long associateId) {
		return CommentDeleteServiceRequest.builder().commentId(commentId).associateId(associateId).build();
	}
}
