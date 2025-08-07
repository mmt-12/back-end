package com.memento.server.member;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.domain.member.Member;

public class MemberFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String NAME = "김싸피";
	private static final String EMAIL = "example@naver.com";
	private static final LocalDate BIRTH_DATE = LocalDate.of(2000, 1, 1);
	private static final Long KAKAO_ID = 1L;

	public static Member member() {
		return Member.builder()
			.id(idGenerator.getAndIncrement())
			.name(NAME)
			.email(EMAIL)
			.brithday(BIRTH_DATE)
			.kakaoId(KAKAO_ID)
			.build();
	}
}
