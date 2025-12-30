package com.memento.server.domain.member;

import java.time.LocalDate;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByKakaoIdAndDeletedAtIsNull(Long kakaoId);

	Optional<Member> findByIdAndDeletedAtIsNull(Long id);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByBirthday(LocalDate birthday);
}
