package com.memento.server.domain.community;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record AssociateExclusiveAchievementEvent(
	Long associateId,
	LocalDate birthDay
) {

	public static AssociateExclusiveAchievementEvent from(Long associateId, LocalDate birthDay){
		return AssociateExclusiveAchievementEvent.builder()
			.associateId(associateId)
			.birthDay(birthDay)
			.build();
	}
}
