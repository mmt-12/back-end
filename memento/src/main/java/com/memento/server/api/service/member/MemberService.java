package com.memento.server.api.service.member;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.member.MemberSignUpResponse;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public Optional<Member> findMemberWithKakaoId(Long kakaoId) {
		return memberRepository.findByKakaoId(kakaoId);
	}

	public MemberSignUpResponse signUp(Long kakaoId, String name, String email, LocalDate birthday) {
		Optional<Member> memberOptional = memberRepository.findByKakaoId(kakaoId);

		if (memberOptional.isPresent()) {
			throw new DuplicateKeyException("이미 가입된 회원입니다.");
		}

		Member member = memberRepository.save(Member.builder()
			.name(name)
			.email(email)
			.birthday(birthday)
			.kakaoId(kakaoId)
			.build());

		MemberClaim memberClaim = MemberClaim.builder().memberId(member.getId()).build();
		JwtToken token = jwtTokenProvider.createToken(memberClaim);
		return MemberSignUpResponse.builder()
			.memberId(member.getId())
			.name(member.getName())
			.token(token)
			.build();
	}

	public void update(Long memberId, String name, String email) {
	}
}
