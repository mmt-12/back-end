package com.memento.server.spring.domain.community;

import static com.memento.server.common.error.ErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.member.Member;
import com.memento.server.member.MemberFixtures;

public class CommunityTest {

	private static final String VALID_NAME = "community";
	private static final Member VALID_MEMBER = MemberFixtures.member();

	@Test
	@DisplayName("커뮤니티를 생성한다.")
	void create() {
	    // when
		Community community = Community.create(VALID_NAME, VALID_MEMBER);

	    // then
		assertThat(community).isNotNull();
		assertThat(community.getName()).isEqualTo(VALID_NAME);
		assertThat(community.getMember()).isEqualTo(VALID_MEMBER);
	}

	@Test
	@DisplayName("커뮤니티 생성 시 이름이 null이면 COMMUNITY_NAME_REQUIRED 예외가 발생한다.")
	void createCommunity_withNullName_throwsException() {
		assertThatThrownBy(() -> Community.create(null, VALID_MEMBER))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", COMMUNITY_NAME_REQUIRED);
	}

	@Test
	@DisplayName("커뮤니티 생성 시 이름이 공백이면 COMMUNITY_NAME_BLANK 예외가 발생한다.")
	void createCommunity_withBlankName_throwsException() {
		assertThatThrownBy(() -> Community.create("   ", VALID_MEMBER))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", COMMUNITY_NAME_BLANK);
	}

	@Test
	@DisplayName("커뮤니티 생성 시 이름이 102자를 초과하면 COMMUNITY_NAME_TOO_LONG 예외가 발생한다.")
	void createCommunity_withTooLongName_throwsException() {
		// given
		String tooLongName = "a".repeat(103);

		// when && then
		assertThatThrownBy(() -> Community.create(tooLongName, VALID_MEMBER))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", COMMUNITY_NAME_TOO_LONG);
	}

	@Test
	@DisplayName("커뮤니티 생성 시 member가 null이면 COMMUNITY_MEMBER_REQUIRED 예외가 발생한다.")
	void createCommunity_withNullMember_throwsException() {
		assertThatThrownBy(() -> Community.create(VALID_NAME, null))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", COMMUNITY_MEMBER_REQUIRED);
	}
}
