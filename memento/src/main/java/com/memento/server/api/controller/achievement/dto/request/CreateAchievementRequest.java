package com.memento.server.api.controller.achievement.dto.request;

import lombok.Builder;

@Builder
public record CreateAchievementRequest(
	String content
) {
}
