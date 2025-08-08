package com.memento.server.utility.validation.associate;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_COMMUNITY_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_MEMBER_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NICKNAME_BLANK;
import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NICKNAME_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NICKNAME_TOO_LONG;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.member.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssociateValidator {

	private static final int MAX_NICKNAME_LENGTH = 51;

	public static void validateNickname(String nickname) {
		if (nickname == null) {
			throw new MementoException(ASSOCIATE_NICKNAME_REQUIRED);
		}
		if (nickname.isBlank()) {
			throw new MementoException(ASSOCIATE_NICKNAME_BLANK);
		}
		if (nickname.length() > MAX_NICKNAME_LENGTH) {
			throw new MementoException(ASSOCIATE_NICKNAME_TOO_LONG);
		}
	}

	public static void validateMember(Member member) {
		if (member == null) {
			throw new MementoException(ASSOCIATE_MEMBER_REQUIRED);
		}
	}

	public static void validateCommunity(Community community) {
		if (community == null) {
			throw new MementoException(ASSOCIATE_COMMUNITY_REQUIRED);
		}
	}
}
