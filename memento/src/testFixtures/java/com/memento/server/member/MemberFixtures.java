package com.memento.server.member;

import java.time.LocalDate;

import com.memento.server.domain.member.Member;

public class MemberFixtures {

	private static final String NAME = "김싸피";
	private static final String EMAIL = "example@naver.com";
	private static final LocalDate BIRTHDAY = LocalDate.of(1999, 1, 1);
	private static final Long KAKAO_ID = 1L;

	public static Member member() {
		return Member.create(NAME, EMAIL, BIRTHDAY, KAKAO_ID);
	}
}
