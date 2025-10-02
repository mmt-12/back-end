package com.memento.server.associate;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.member.Member;
import com.memento.server.member.MemberFixtures;

public class AssociateFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String NICKNAME = "nickname";
	private static final String PROFILE_IMAGE_URL = "https://example.com/profile_image/image.png";

	public static Associate associate() {
		return Associate.builder()
			.id(idGenerator.getAndIncrement())
			.nickname(NICKNAME)
			.profileImageUrl(PROFILE_IMAGE_URL)
			.member(MemberFixtures.member())
			.community(CommunityFixtures.community())
			.build();
	}

	public static Associate associate(Member member, Community community) {
		return Associate.builder()
			.nickname(NICKNAME)
			.member(member)
			.community(community)
			.build();
	}
}
