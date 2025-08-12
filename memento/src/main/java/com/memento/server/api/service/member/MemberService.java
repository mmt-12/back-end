package com.memento.server.api.service.member;

import static com.memento.server.common.error.ErrorCodes.MEMBER_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NOT_FOUND;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

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

	@Transactional
	public MemberSignUpResponse signUp(Long kakaoId, String name, String email, LocalDate birthday) {
		Optional<Member> memberOptional = memberRepository.findByKakaoId(kakaoId);
		if (memberOptional.isPresent()) {
			throw new MementoException(MEMBER_DUPLICATE);
		}

		Member member = memberRepository.save(Member.create(name, email, birthday, kakaoId));

		MemberClaim memberClaim = MemberClaim.from(member);
		JwtToken token = jwtTokenProvider.createToken(memberClaim);
		return MemberSignUpResponse.from(member, token);
	}

	@Transactional
	public void update(Long memberId, String name, String email) {
		Optional<Member> memberOptional = memberRepository.findById(memberId);
		if (memberOptional.isEmpty()) {
			throw new MementoException(MEMBER_NOT_FOUND);
		}

		Member member = memberOptional.get();
		member.update(name, email);
	}
}
