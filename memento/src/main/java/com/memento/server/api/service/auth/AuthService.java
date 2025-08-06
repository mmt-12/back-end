package com.memento.server.api.service.auth;

import org.springframework.stereotype.Service;

import com.memento.server.client.oauth.KakaoClient;
import com.memento.server.api.controller.auth.AuthGuestResponse;
import com.memento.server.api.controller.auth.AuthMemberResponse;
import com.memento.server.api.controller.auth.AuthResponse;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.api.service.member.MemberService;
import com.memento.server.api.service.oauth.KakaoOpenIdDecoder;
import com.memento.server.api.service.oauth.KakaoOpenIdPayload;
import com.memento.server.api.service.oauth.KakaoToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final KakaoClient kakaoClient;
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;
	private final KakaoOpenIdDecoder kakaoOpenIdDecoder;

	public String getAuthUrl() {
		return kakaoClient.getAuthUrl();
	}

	public AuthResponse handleAuthorizationCallback(String code) {
		KakaoToken kakaoToken = kakaoClient.getKakaoToken(code);
		KakaoOpenIdPayload openIdPayload = kakaoOpenIdDecoder.validateOpenIdToken(kakaoToken.idToken());
		Long kakaoId = Long.parseLong(openIdPayload.sub());

		return memberService.findMemberWithKakaoId(kakaoId)
			.<AuthResponse>map(member -> {
				MemberClaim memberClaim = MemberClaim.builder()
					.memberId(member.getId())
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
