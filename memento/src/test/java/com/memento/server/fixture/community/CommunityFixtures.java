package com.memento.server.fixture.community;

import com.memento.server.domain.community.Community;
import com.memento.server.fixture.member.MemberFixtures;

public class CommunityFixtures {
	public static Community community() {
		return Community.create("12반", MemberFixtures.member());
	}
}
