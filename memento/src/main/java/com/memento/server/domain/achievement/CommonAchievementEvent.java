package com.memento.server.domain.achievement;

import lombok.Builder;

@Builder
public record CommonAchievementEvent(
	Long associateId,
	Long achievementId
) {

	public static CommonAchievementEvent of(Long associateId, Long achievementId){
		return CommonAchievementEvent.builder()
			.associateId(associateId)
			.achievementId(achievementId)
			.build();
	}
}
