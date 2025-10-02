package com.memento.server.utility.validation.member;

import static com.memento.server.common.error.ErrorCodes.MEMBER_BIRTHDAY_IN_FUTURE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_EMAIL_BLANK;
import static com.memento.server.common.error.ErrorCodes.MEMBER_EMAIL_INVALID_FORMAT;
import static com.memento.server.common.error.ErrorCodes.MEMBER_EMAIL_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.MEMBER_EMAIL_TOO_LONG;
import static com.memento.server.common.error.ErrorCodes.MEMBER_KAKAO_ID_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NAME_BLANK;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NAME_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NAME_TOO_LONG;

import java.time.LocalDate;
import java.util.regex.Pattern;

import com.memento.server.common.exception.MementoException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberValidator {

	private static final int MAX_NAME_LENGTH = 102;
	private static final int MAX_EMAIL_LENGTH = 255;
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	public static void validateName(String name) {
		if (name == null) {
			throw new MementoException(MEMBER_NAME_REQUIRED);
		}
		if (name.isBlank()) {
			throw new MementoException(MEMBER_NAME_BLANK);
		}
		if (name.length() > MAX_NAME_LENGTH) {
			throw new MementoException(MEMBER_NAME_TOO_LONG);
		}
	}

	public static void validateEmail(String email) {
		if (email == null) {
			throw new MementoException(MEMBER_EMAIL_REQUIRED);
		}
		if (email.isBlank()) {
			throw new MementoException(MEMBER_EMAIL_BLANK);
		}
		if (email.length() > MAX_EMAIL_LENGTH) {
			throw new MementoException(MEMBER_EMAIL_TOO_LONG);
		}
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new MementoException(MEMBER_EMAIL_INVALID_FORMAT);
		}
	}

	public static void validateBirthday(LocalDate birthday) {
		if (birthday != null && birthday.isAfter(LocalDate.now())) {
			throw new MementoException(MEMBER_BIRTHDAY_IN_FUTURE);
		}
	}

	public static void validateKakaoId(Long kakaoId) {
		if (kakaoId == null) {
			throw new MementoException(MEMBER_KAKAO_ID_REQUIRED);
		}
	}
}
