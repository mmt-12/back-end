package com.memento.server.api.controller.achievement.dto;

import lombok.Builder;

@Builder
public record CreateAchievementRequest(
	String content
) {
}
