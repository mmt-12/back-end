package com.memento.server.domain.guestBook;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record GuestBookExclusiveAchievementEvent(
	Long associateId,
	LocalDate from,
	LocalDate to
) {

	public static GuestBookExclusiveAchievementEvent from(Long associateId, LocalDate from, LocalDate to){
		return GuestBookExclusiveAchievementEvent.builder()
			.associateId(associateId)
			.from(from)
			.to(to)
			.build();
	}
}
