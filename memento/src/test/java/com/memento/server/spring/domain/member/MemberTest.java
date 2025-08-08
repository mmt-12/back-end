package com.memento.server.spring.domain.member;

import static com.memento.server.common.error.ErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.member.Member;

public class MemberTest {

	private static final String VALID_NAME = "member";
	private static final String VALID_EMAIL = "member@example.com";
	private static final LocalDate VALID_BIRTHDAY = LocalDate.of(1999, 1, 1);
	private static final Long VALID_KAKAO_ID = 123456789L;

	@Test
	@DisplayName("회원을 생성한다.")
	void create() {
		// when
		Member member = Member.create(VALID_NAME, VALID_EMAIL, VALID_BIRTHDAY, VALID_KAKAO_ID);

		// then
		assertThat(member).isNotNull();
		assertThat(member.getName()).isEqualTo(VALID_NAME);
		assertThat(member.getEmail()).isEqualTo(VALID_EMAIL);
		assertThat(member.getBirthday()).isEqualTo(VALID_BIRTHDAY);
		assertThat(member.getKakaoId()).isEqualTo(VALID_KAKAO_ID);
	}

	@Test
	@DisplayName("회원 생성 시 이름이 null이면 MEMBER_NAME_REQUIRED 예외가 발생한다.")
	void createMember_withNullName_throwsException() {
		assertThatThrownBy(() -> Member.create(null, VALID_EMAIL, VALID_BIRTHDAY, VALID_KAKAO_ID))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_NAME_REQUIRED);
	}

	@Test
	@DisplayName("회원 생성 시 이름이 공백이면 MEMBER_NAME_BLANK 예외가 발생한다.")
	void createMember_withBlankName_throwsException() {
		assertThatThrownBy(() -> Member.create("   ", VALID_EMAIL, VALID_BIRTHDAY, VALID_KAKAO_ID))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_NAME_BLANK);
	}

	@Test
	@DisplayName("회원 생성 시 이름이 102자를 초과하면 MEMBER_NAME_TOO_LONG 예외가 발생한다.")
	void createMember_withTooLongName_throwsException() {
		String tooLongName = "a".repeat(103);

		assertThatThrownBy(() -> Member.create(tooLongName, VALID_EMAIL, VALID_BIRTHDAY, VALID_KAKAO_ID))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_NAME_TOO_LONG);
	}

	@Test
	@DisplayName("회원 생성 시 이메일이 null이면 MEMBER_EMAIL_REQUIRED 예외가 발생한다.")
	void createMember_withNullEmail_throwsException() {
		assertThatThrownBy(() -> Member.create(VALID_NAME, null, VALID_BIRTHDAY, VALID_KAKAO_ID))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_EMAIL_REQUIRED);
	}

	@Test
	@DisplayName("회원 생성 시 이메일이 공백이면 MEMBER_EMAIL_BLANK 예외가 발생한다.")
	void createMember_withBlankEmail_throwsException() {
		assertThatThrownBy(() -> Member.create(VALID_NAME, "   ", VALID_BIRTHDAY, VALID_KAKAO_ID))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_EMAIL_BLANK);
	}

	@Test
	@DisplayName("회원 생성 시 이메일이 255자를 초과하면 MEMBER_EMAIL_TOO_LONG 예외가 발생한다.")
	void createMember_withTooLongEmail_throwsException() {
		String tooLongEmail = "a".repeat(256);

		assertThatThrownBy(() -> Member.create(VALID_NAME, tooLongEmail, VALID_BIRTHDAY, VALID_KAKAO_ID))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_EMAIL_TOO_LONG);
	}

	@Test
	@DisplayName("회원 생성 시 이메일 형식이 잘못되면 MEMBER_EMAIL_INVALID_FORMAT 예외가 발생한다.")
	void createMember_withInvalidFormatEmail_throwsException() {
		assertThatThrownBy(() -> Member.create(VALID_NAME, "invalid-email", VALID_BIRTHDAY, VALID_KAKAO_ID))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_EMAIL_INVALID_FORMAT);
	}

	@Test
	@DisplayName("회원 생성 시 생일이 현재 날짜보다 이후이면 MEMBER_BIRTHDAY_IN_FUTURE 예외가 발생한다.")
	void createMember_withInFuture_throwsException() {
		LocalDate future = LocalDate.now().plusDays(1);

		assertThatThrownBy(() -> Member.create(VALID_NAME, VALID_EMAIL, future, VALID_KAKAO_ID))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_BIRTHDAY_IN_FUTURE);
	}

	@Test
	@DisplayName("회원 생성 시 카카오 ID가 null이면 MEMBER_KAKAO_ID_REQUIRED 예외가 발생한다.")
	void createMember_withNullKakaoId_throwsException() {
		assertThatThrownBy(() -> Member.create(VALID_NAME, VALID_EMAIL, VALID_BIRTHDAY, null))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", MEMBER_KAKAO_ID_REQUIRED);
	}
}