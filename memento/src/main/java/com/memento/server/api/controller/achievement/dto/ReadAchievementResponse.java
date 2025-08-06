package com.memento.server.api.controller.achievement.dto;

import java.util.List;

import com.memento.server.domain.achievement.AchievementType;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record ReadAchievementResponse(
	List<Achievement> achievements
) {
	public static ReadAchievementResponse from() {
		Achievement ach1 = new Achievement(1L, "뤼전드", "오준수 전용", true, AchievementType.HIDDEN);
		Achievement ach2 = new Achievement(2L, "GMG", "기억 다수 참여", true, AchievementType.OPEN);

		return new ReadAchievementResponse(List.of(ach1, ach2));
	}

	@Getter
	@AllArgsConstructor
	public static class Achievement {
		Long id;
		String name;
		String criteria;
		boolean isObtained;
		AchievementType type;
	}
}
