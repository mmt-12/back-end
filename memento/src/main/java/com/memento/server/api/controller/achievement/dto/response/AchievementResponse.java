package com.memento.server.api.controller.achievement.dto.response;

import com.memento.server.api.service.memory.dto.Author;
import com.memento.server.api.service.memory.dto.MemoryItem;
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

	public static AchievementResponse from(MemoryItem.AchievementDto achievement) {
		if (achievement == null) {
			return null;
		}

		return AchievementResponse.builder()
			.id(achievement.id())
			.name(achievement.name())
			.build();
	}

	public static AchievementResponse from(Author.Achievement achievement) {
		if (achievement == null) {
			return null;
		}

		return AchievementResponse.builder()
			.id(achievement.id())
			.name(achievement.name())
			.build();
	}
}
