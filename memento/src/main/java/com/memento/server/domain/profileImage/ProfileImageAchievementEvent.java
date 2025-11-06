package com.memento.server.domain.profileImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record ProfileImageAchievementEvent(Long associateId,
										   Type type) {

	@Getter
	@AllArgsConstructor
	public enum Type {
		REGISTERED("등록당한"),
		UPLOADED("등록한");

		private final String displayName;
	}

	public static ProfileImageAchievementEvent of(Long associateId, Type type) {
		return ProfileImageAchievementEvent.builder()
			.associateId(associateId)
			.type(type)
			.build();
	}
}
