package com.memento.server.api.service.memory.dto;

import com.memento.server.domain.community.Associate;

import lombok.Builder;

@Builder
public record Author(
	Long id,
	String imageUrl,
	String nickname,
	Achievement achievement
) {

	@Builder
	public record Achievement(
		Long id,
		String name
	) {
	}

	public static Author of(Associate associate, com.memento.server.domain.achievement.Achievement achievement) {
		return Author.builder()
			.id(associate.getId())
			.nickname(associate.getNickname())
			.imageUrl(associate.getProfileImageUrl())
			.achievement(achievement == null ? null : Author.Achievement.builder()
				.id(achievement.getId())
				.name(achievement.getName())
				.build())
			.build();
	}
}
