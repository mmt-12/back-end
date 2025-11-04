package com.memento.server.api.service.achievement.dto.response;

import java.util.List;

import com.memento.server.domain.achievement.AchievementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SearchAchievementResponse {
	private List<Achievement> achievements;

	@Getter
	@Builder
	@AllArgsConstructor
	public static class Achievement {
		Long id;
		String name;
		String criteria;
		AchievementType type;
		boolean isObtained;
	}

	public static SearchAchievementResponse from(List<Achievement> achievements) {
		return SearchAchievementResponse.builder()
			.achievements(achievements)
			.build();
	}
}

