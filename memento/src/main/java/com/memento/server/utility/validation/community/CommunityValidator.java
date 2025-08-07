package com.memento.server.utility.validation.community;

import static com.memento.server.common.error.ErrorCodes.COMMUNITY_MEMBER_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NAME_BLANK;
import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NAME_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NAME_TOO_LONG;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.member.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommunityValidator {

	private static final int MAX_NAME_LENGTH = 102;

	public static void validateName(String name) {
		if (name == null) {
			throw new MementoException(COMMUNITY_NAME_REQUIRED);
		}
		if (name.isBlank()) {
			throw new MementoException(COMMUNITY_NAME_BLANK);
		}
		if (name.length() > MAX_NAME_LENGTH) {
			throw new MementoException(COMMUNITY_NAME_TOO_LONG);
		}
	}

	public static void validateMember(Member member) {
		if (member == null) {
			throw new MementoException(COMMUNITY_MEMBER_REQUIRED);
		}
	}
}
