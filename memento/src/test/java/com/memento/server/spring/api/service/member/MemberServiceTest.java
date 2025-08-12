package com.memento.server.spring.api.service.member;

import static com.memento.server.common.error.ErrorCodes.MEMBER_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.service.member.MemberService;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

@SpringBootTest
@Transactional
class MemberServiceTest {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("회원 가입을 한다.")
	void signup() {
		// given
		Long kakaoId = 1001L;
		String name = "홍길동";
		String email = "hong@test.com";
		LocalDate birthday = LocalDate.of(1990, 1, 1);

		// when
		MemberSignUpResponse response = memberService.signUp(kakaoId, name, email, birthday);

		// then
		Member saved = memberRepository.findByKakaoId(kakaoId).orElseThrow();
		assertThat(saved.getName()).isEqualTo(name);
		assertThat(saved.getEmail()).isEqualTo(email);
		assertThat(response.memberId()).isEqualTo(saved.getId());
		assertThat(response.token()).isNotNull();
		assertThat(saved.getKakaoId()).isEqualTo(kakaoId);
	}

	@Test
	@DisplayName("회원 가입 시 이미 가입한 카카오 아이디라면 MEMBER_DUPLICATE 예외가 발생한다.")
	void signup_withDuplicate_throwsException() {
		// given
		Long kakaoId = 1001L;
		memberRepository.save(Member.create("홍길동", "hong@test.com", LocalDate.of(1990, 1, 1), kakaoId));

		// when & then
		assertThatThrownBy(() ->
			memberService.signUp(kakaoId, "아무개", "any@test.com", LocalDate.of(1995, 5, 5))
		)
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException)ex;
				assertThat(me.getErrorCode()).isEqualTo(MEMBER_DUPLICATE);
			});
	}

	@Test
	@DisplayName("회원 정보를 수정한다.")
	void update() {
		// given
		Member member = memberRepository.save(Member.create("홍길동", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));

		// when
		memberService.update(member.getId(), "김철수", "kim@test.com");

		// then
		Member updated = memberRepository.findById(member.getId()).orElseThrow();
		assertThat(updated.getName()).isEqualTo("김철수");
		assertThat(updated.getEmail()).isEqualTo("kim@test.com");
	}

	@Test
	@DisplayName("회원 정보를 수정할 때 회원 조회에 실패하면 MEMBER_NOT_FOUND 예외가 발생한다.")
	void update_withNull_throwsException() {
		// given
		Long invalidId = 9999L;

		// when & then
		assertThatThrownBy(() ->
			memberService.update(invalidId, "김철수", "kim@test.com")
		)
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException)ex;
				assertThat(me.getErrorCode()).isEqualTo(MEMBER_NOT_FOUND);
			});
	}
}
