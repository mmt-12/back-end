package com.memento.server.spring.domain.community;

import static com.memento.server.common.error.ErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.member.Member;
import com.memento.server.member.MemberFixtures;
import com.memento.server.community.CommunityFixtures;

public class AssociateTest {

	private static final String VALID_NICKNAME = "열정적인참여자";
	private static final Member VALID_MEMBER = MemberFixtures.member();
	private static final Community VALID_COMMUNITY = CommunityFixtures.community();

	@Test
	@DisplayName("그룹 참여자를 생성한다.")
	void create() {
		// when
		Associate associate = Associate.create(VALID_NICKNAME, VALID_MEMBER, VALID_COMMUNITY);

		// then
		assertThat(associate).isNotNull();
		assertThat(associate.getNickname()).isEqualTo(VALID_NICKNAME);
		assertThat(associate.getMember()).isEqualTo(VALID_MEMBER);
		assertThat(associate.getCommunity()).isEqualTo(VALID_COMMUNITY);
	}

	@Test
	@DisplayName("그룹 참여자 생성 시 닉네임이 null이면 ASSOCIATE_NICKNAME_REQUIRED 예외가 발생한다.")
	void create_withNullNickname_throwsException() {
		assertThatThrownBy(() -> Associate.create(null, VALID_MEMBER, VALID_COMMUNITY))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NICKNAME_REQUIRED);
	}

	@Test
	@DisplayName("그룹 참여자 생성 시 닉네임이 공백이면 ASSOCIATE_NICKNAME_BLANK 예외가 발생한다.")
	void create_withBlankNickname_throwsException() {
		assertThatThrownBy(() -> Associate.create("   ", VALID_MEMBER, VALID_COMMUNITY))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NICKNAME_BLANK);
	}

	@Test
	@DisplayName("그룹 참여자 생성 시 닉네임이 51자를 초과하면 ASSOCIATE_NICKNAME_TOO_LONG 예외가 발생한다.")
	void create_withTooLongNickname_throwsException() {
		String tooLongNickname = "a".repeat(52);

		assertThatThrownBy(() -> Associate.create(tooLongNickname, VALID_MEMBER, VALID_COMMUNITY))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_NICKNAME_TOO_LONG);
	}

	@Test
	@DisplayName("그룹 참여자 생성 시 회원이 null이면 ASSOCIATE_MEMBER_REQUIRED 예외가 발생한다.")
	void create_withNullMember_throwsException() {
		assertThatThrownBy(() -> Associate.create(VALID_NICKNAME, null, VALID_COMMUNITY))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_MEMBER_REQUIRED);
	}

	@Test
	@DisplayName("그룹 참여자 생성 시 커뮤니티가 null이면 ASSOCIATE_COMMUNITY_REQUIRED 예외가 발생한다.")
	void create_withNullCommunity_throwsException() {
		assertThatThrownBy(() -> Associate.create(VALID_NICKNAME, VALID_MEMBER, null))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ASSOCIATE_COMMUNITY_REQUIRED);
	}
}