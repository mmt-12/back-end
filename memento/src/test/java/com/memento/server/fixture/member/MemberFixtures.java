package com.memento.server.fixture.member;

import java.time.LocalDate;

import com.memento.server.domain.member.Member;

public class MemberFixtures {

	public static Member member() {
		return Member.create("오준수", "example@naver.com", LocalDate.of(1999, 10, 13), 1L);
	}
}
