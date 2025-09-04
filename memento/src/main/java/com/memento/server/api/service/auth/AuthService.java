package com.memento.server.api.service.auth;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.auth.dto.AuthGuestResponse;
import com.memento.server.api.controller.auth.dto.AuthMemberResponse;
import com.memento.server.api.controller.auth.dto.AuthResponse;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.api.service.member.MemberService;
import com.memento.server.api.service.oauth.KakaoOpenIdDecoder;
import com.memento.server.api.service.oauth.KakaoOpenIdPayload;
import com.memento.server.api.service.oauth.KakaoToken;
import com.memento.server.client.oauth.KakaoClient;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final KakaoClient kakaoClient;
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;
	private final KakaoOpenIdDecoder kakaoOpenIdDecoder;
	private final AssociateRepository associateRepository;

	public String getAuthUrl() {
		return kakaoClient.getAuthUrl();
	}

	public AuthResponse handleAuthorizationCallback(String code) {
		KakaoToken kakaoToken = kakaoClient.getKakaoToken(code);
		KakaoOpenIdPayload openIdPayload = kakaoOpenIdDecoder.validateOpenIdToken(kakaoToken.idToken());
		Long kakaoId = Long.parseLong(openIdPayload.sub());

		return memberService.findMemberWithKakaoId(kakaoId)
			.<AuthResponse>map(member -> {
				// 커뮤니티 자동 선택
				Associate associate = associateRepository.findByMemberIdAndDeletedAtIsNull(member.getId())
					.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));

				MemberClaim memberClaim = MemberClaim.builder()
					.memberId(member.getId())
					.communityId(associate.getCommunity().getId())
					.associateId(associate.getId())
					.build();
				JwtToken token = jwtTokenProvider.createToken(memberClaim);

				return AuthMemberResponse.builder()
					.memberId(member.getId())
					.name(member.getName())
					.token(token)
					.build();
			})
			.orElseGet(() -> {
				MemberClaim memberClaim = MemberClaim.builder()
					.memberId(kakaoId)
					.build();
				JwtToken token = jwtTokenProvider.createTempToken(memberClaim);

				return AuthGuestResponse.builder()
					.kakaoId(kakaoId)
					.email(openIdPayload.email())
					.token(token)
					.build();
			});
	}
}
