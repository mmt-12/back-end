package com.memento.server.domain.memory;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record MemoryAchievementEvent(
	List<Long> associateIds,
	Type type
) {

	@Getter
	@AllArgsConstructor
	public enum Type{
		CREATE("생성"),
		JOINED("참여");

		private final String displayName;
	}

	public static MemoryAchievementEvent from(List<Long> associateIds,Type type){
		return MemoryAchievementEvent.builder()
			.associateIds(associateIds)
			.type(type)
			.build();
	}
}
