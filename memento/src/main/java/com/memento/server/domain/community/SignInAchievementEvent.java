package com.memento.server.domain.community;

import lombok.Builder;

@Builder
public record SignInAchievementEvent(
	Long associateId
) {

	public static SignInAchievementEvent from(Long associateId) {
		return SignInAchievementEvent.builder()
			.associateId(associateId)
			.build();
	}
}
