package com.memento.server.service.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.memento.server.client.oauth.KakaoClient;
import com.memento.server.controller.auth.AuthResponse;
import com.memento.server.service.member.MemberService;
import com.memento.server.service.oauth.KakaoOpenIdDecoder;
import com.memento.server.service.auth.jwt.JwtToken;
import com.memento.server.service.auth.jwt.JwtTokenProvider;
import com.memento.server.service.auth.jwt.MemberClaim;
import com.memento.server.service.oauth.KakaoOpenIdPayload;
import com.memento.server.service.oauth.KakaoToken;

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

	public ResponseEntity<AuthResponse> handleAuthorizationCallback(String code) {
		KakaoToken kakaoToken = kakaoClient.getKakaoToken(code);
		KakaoOpenIdPayload openId = kakaoOpenIdDecoder.validateOpenIdToken(kakaoToken.idToken());

		return memberService.findMemberWithKakaoId(openId.sub())
			.map(member -> {
				MemberClaim memberClaim = MemberClaim.builder().memberId(member.getId()).build();

				JwtToken token = jwtTokenProvider.createToken(memberClaim);
				return ResponseEntity.ok(new AuthResponse(member.getId(), member.getName(), token));
			})
			.orElseGet(() -> ResponseEntity.ok(new AuthResponse(null, openId.sub(), null)));
	}
}
