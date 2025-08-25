package com.memento.server.api.service.achievement.dto;

import com.memento.server.domain.achievement.AchievementType;

public record SearchAchievementDto(
	Long id,
	String name,
	String criteria,
	AchievementType type,
	Boolean isObtained
) {
}
