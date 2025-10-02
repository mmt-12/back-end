package com.memento.server.domain.mbti;

import lombok.Builder;

@Builder
public record MbtiAchievementEvent(
	Long fromAssociateId,
	Long toAssociateId
) {


	public static MbtiAchievementEvent from(Long fromAssociateId, Long toAssociateId) {
		return MbtiAchievementEvent.builder()
			.fromAssociateId(fromAssociateId)
			.toAssociateId(toAssociateId)
			.build();
	}
}
