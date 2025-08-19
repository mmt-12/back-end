package com.memento.server.api.controller.achievement.dto;

import java.util.List;

import com.memento.server.domain.achievement.AchievementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record SearchAchievementResponse(
	List<Achievement> achievements
) {
	@Getter
	@Builder
	@AllArgsConstructor
	public static class Achievement {
		Long id;
		String name;
		String criteria;
		boolean isObtained;
		AchievementType type;
	}
}
