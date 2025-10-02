package com.memento.server.community;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.domain.community.Community;
import com.memento.server.domain.member.Member;
import com.memento.server.member.MemberFixtures;

public class CommunityFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String NAME = "community";

	public static Community community() {
		return Community.builder()
			.id(idGenerator.getAndIncrement())
			.name(NAME)
			.member(MemberFixtures.member())
			.build();
	}

	public static Community community(Member member) {
		return Community.builder()
			.name(NAME)
			.member(member)
			.build();
	}

	public static Community community(Member member, String name) {
		return Community.builder()
			.name(name)
			.member(member)
			.build();
	}
}
