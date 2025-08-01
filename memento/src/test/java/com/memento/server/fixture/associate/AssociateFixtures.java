package com.memento.server.fixture.associate;

import com.memento.server.domain.community.Associate;
import com.memento.server.fixture.achievement.AchievementFixtures;
import com.memento.server.fixture.community.CommunityFixtures;
import com.memento.server.fixture.member.MemberFixtures;

public class AssociateFixtures {

	public static Associate associate() {
		return Associate.create("오비빔", "https://example.com/image.png", "안녕? 나는 오비빔이야.",
			AchievementFixtures.achievement(), MemberFixtures.member(), CommunityFixtures.community());
	}
}
