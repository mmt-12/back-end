package com.memento.server.domain.post;

import lombok.Builder;

@Builder
public record PostImageAchievementEvent(
	Long associateId
) {

	public static PostImageAchievementEvent from(Long associateId){
		return PostImageAchievementEvent.builder()
			.associateId(associateId)
			.build();
	}
}
