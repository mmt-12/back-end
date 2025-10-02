package com.memento.server.domain.community;

import lombok.Builder;

@Builder
public record SignInAchievementEvent(
	Long associateId
) {
}
