package com.memento.server.achievement;

import static com.memento.server.domain.achievement.AchievementType.*;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.domain.achievement.Achievement;

public class AchievementFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String NAME = "achievement";
	private static final String CRITERIA = "criteria";

	public static Achievement achievement() {
		return Achievement.builder()
			.id(idGenerator.getAndIncrement())
			.name(NAME)
			.criteria(CRITERIA)
			.type(OPEN)
			.build();
	}
}
