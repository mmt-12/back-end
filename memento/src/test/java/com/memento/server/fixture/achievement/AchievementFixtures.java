package com.memento.server.fixture.achievement;

import com.memento.server.domain.achievement.Achievement;

public class AchievementFixtures {

	public static Achievement achievement() {
		return Achievement.create("뤼전드", "오준수의 히든 업적");
	}
}
