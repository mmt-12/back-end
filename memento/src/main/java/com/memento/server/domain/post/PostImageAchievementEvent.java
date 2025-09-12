package com.memento.server.domain.post;

import lombok.Builder;

@Builder
public record PostImageAchievementEvent(
	Long associateId,
	Long postId
) {

	public static PostImageAchievementEvent from(Long associateId, Long postId){
		return PostImageAchievementEvent.builder()
			.associateId(associateId)
			.postId(postId)
			.build();
	}
}
