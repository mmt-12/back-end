package com.memento.server.domain.reaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record ReactionAchievementEvent(
	Long associateId,
	Type type,
	Long reactionId,
	ReactionType reactionType
) {

	@Getter
	@AllArgsConstructor
	public enum Type{
		REGISTRANT("등록"),
		USE("사용");

		private final String displayName;
	}

	@Getter
	@AllArgsConstructor
	public enum ReactionType{
		EMOJI("이모티콘"),
		VOICE("보이스");

		private final String displayName;
	}

	public static ReactionAchievementEvent fromRegistrant(Long associateId, Type type, Long reactionId, ReactionType reactionType) {
		return ReactionAchievementEvent.builder()
			.associateId(associateId)
			.type(type)
			.reactionId(reactionId)
			.reactionType(reactionType)
			.build();
	}

	public static ReactionAchievementEvent fromUse(Long associateId, Type type) {
		return ReactionAchievementEvent.builder()
			.associateId(associateId)
			.type(type)
			.build();
	}
}
