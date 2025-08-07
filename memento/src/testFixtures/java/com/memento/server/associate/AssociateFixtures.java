package com.memento.server.associate;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.achievement.AchievementFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.member.MemberFixtures;

public class AssociateFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String NICKNAME = "nickname";
	private static final String PROFILE_IMAGE_URL = "https://example.com/image.png";
	private static final String INTRODUCTION = "introduction";

	public static Associate associate() {
		return Associate.builder()
			.id(idGenerator.getAndIncrement())
			.nickname(NICKNAME)
			.profileImageUrl(PROFILE_IMAGE_URL)
			.introduction(INTRODUCTION)
			.member(MemberFixtures.member())
			.community(CommunityFixtures.community())
			.achievement(AchievementFixtures.achievement())
			.build();
	}
}
