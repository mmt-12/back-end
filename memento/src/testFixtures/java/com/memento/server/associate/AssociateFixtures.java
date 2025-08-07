package com.memento.server.associate;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.member.MemberFixtures;

public class AssociateFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String NICKNAME = "nickname";

	public static Associate associate() {
		return Associate.builder()
			.id(idGenerator.getAndIncrement())
			.nickname(NICKNAME)
			.member(MemberFixtures.member())
			.community(CommunityFixtures.community())
			.build();
	}
}
