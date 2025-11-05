package com.memento.server.api.controller.achievement.dto.response;

import com.memento.server.domain.achievement.Achievement;

import lombok.Builder;

@Builder
public record AchievementResponse(
	Long id,
	String name
) {

	public static AchievementResponse from(Achievement achievement) {
		if (achievement == null) {
			return null;
		}

		return AchievementResponse.builder()
			.id(achievement.getId())
			.name(achievement.getName())
			.build();
	}
}
