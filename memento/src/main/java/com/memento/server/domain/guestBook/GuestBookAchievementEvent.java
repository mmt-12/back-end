package com.memento.server.domain.guestBook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record GuestBookAchievementEvent(
	Long associateId,
	Long guestBookId,
	Type type
) {

	@Getter
	@AllArgsConstructor
	public enum Type {
		COUNT("갯수체크"),
		WORD("단어체크");

		private final String displayName;
	}

	public static GuestBookAchievementEvent from(Long associateId, Long guestBookId, GuestBookAchievementEvent.Type type) {
		return GuestBookAchievementEvent.builder()
			.associateId(associateId)
			.guestBookId(guestBookId)
			.type(type)
			.build();
	}
}
