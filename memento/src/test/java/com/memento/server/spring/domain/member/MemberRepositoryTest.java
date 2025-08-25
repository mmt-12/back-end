package com.memento.server.spring.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

@DataJpaTest
@EnableJpaAuditing
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("카카오 ID와 삭제되지 않은 상태로 회원을 조회한다")
	void findByKakaoIdAndDeletedAtIsNull() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));

		// when
		Optional<Member> foundMember = memberRepository.findByKakaoIdAndDeletedAtIsNull(member.getKakaoId());

		// then
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().getKakaoId()).isEqualTo(member.getKakaoId());
	}

	@Test
	@DisplayName("ID와 삭제되지 않은 상태로 회원을 조회한다")
	void findByIdAndDeletedAtIsNull() {
		// given
		Member member = memberRepository.save(Member.create("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));

		// when
		Optional<Member> foundMember = memberRepository.findByIdAndDeletedAtIsNull(member.getId());

		// then
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().getId()).isEqualTo(member.getId());
	}
}
