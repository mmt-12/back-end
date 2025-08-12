package com.memento.server.api.controller.community.dto;

import java.util.ArrayList;
import java.util.List;

import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;

import lombok.Builder;

@Builder
public record AssociateListResponse(
	String communityName,
	List<AssociateResponse> associates,
	Long cursor,
	Boolean hasNext
) {

	@Builder
	public record AssociateResponse(
		Long id,
		String nickname,
		String imageUrl,
		String introduction,
		AchievementResponse achievement
	) {

		@Builder
		public record AchievementResponse(
			Long id,
			String name
		) {
			public static AchievementResponse from(Achievement achievement) {
				return AchievementResponse.builder()
					.id(achievement.getId())
					.name(achievement.getName())
					.build();
			}
		}

		public static AssociateResponse from(Associate associate) {
			return AssociateResponse.builder()
				.id(associate.getId())
				.nickname(associate.getNickname())
				.imageUrl(associate.getProfileImageUrl())
				.introduction(associate.getIntroduction())
				.achievement(AchievementResponse.from(associate.getAchievement()))
				.build();
		}
	}

	public static AssociateListResponse from(List<Associate> associates, Community community, Integer size) {
		List<AssociateResponse> associatesResult = new ArrayList<>();
		for (Associate associate : associates) {
			associatesResult.add(
				AssociateResponse.from(associate)
			);
		}

		return AssociateListResponse.builder()
			.communityName(community.getName())
			.associates(associatesResult)
			.cursor(associates.size() > size ? associates.get(size).getId() : associates.getLast().getId())
			.hasNext(associates.size() > size)
			.build();
	}
}
