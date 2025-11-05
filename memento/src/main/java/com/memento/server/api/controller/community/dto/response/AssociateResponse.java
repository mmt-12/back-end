package com.memento.server.api.controller.community.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.memento.server.api.controller.achievement.dto.response.AchievementResponse;
import com.memento.server.domain.community.Associate;

import lombok.Builder;

@Builder
public record AssociateResponse(
	Long id,
	String nickname,
	String imageUrl,
	String introduction,
	AchievementResponse achievement
) {

	public static AssociateResponse from(Associate associate) {
		return AssociateResponse.builder()
			.id(associate.getId())
			.nickname(associate.getNickname())
			.imageUrl(associate.getProfileImageUrl())
			.introduction(associate.getIntroduction())
			.achievement(AchievementResponse.from(associate.getAchievement()))
			.build();
	}

	public static List<AssociateResponse> from(List<Associate> associates) {
		List<AssociateResponse> result = new ArrayList<>();
		for (Associate associate : associates) {
			result.add(AssociateResponse.from(associate));
		}

		return result;
	}
}
