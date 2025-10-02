package com.memento.server.domain.achievement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AchievementType {
	OPEN("공개"),
	RESTRICTED("칭호 공개"),
	HIDDEN("비밀");

	private final String displayName;
}
