package com.memento.server.service.auth;

import java.util.Base64;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.memento.server.client.oauth.KakaoClient;
import com.memento.server.client.oauth.KakaoOpenIdPayload;
import com.memento.server.client.oauth.KakaoToken;
import com.memento.server.controller.auth.AuthResponse;
import com.memento.server.controller.auth.AuthToken;
import com.memento.server.service.member.MemberService;
import com.memento.server.service.oauth.KakaoOpenIdTokenVerifier;
import com.memento.server.utility.JsonMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final KakaoClient kakaoClient;
	private final MemberService memberService;
	private final AuthTokenGenerator authTokenGenerator;
	private final KakaoOpenIdTokenVerifier kakaoOpenIdTokenVerifier;

	public String getAuthUrl() {
		return kakaoClient.getAuthUrl();
	}

	public ResponseEntity<AuthResponse> handleAuthorizationCallback(String code) {
		KakaoToken kakaoToken = kakaoClient.getKakaoToken(code);
		String idToken = kakaoToken.idToken();
		kakaoOpenIdTokenVerifier.verifyOpenIdToken(idToken);

		KakaoOpenIdPayload openId = parseOpenIdToken(kakaoToken.idToken());
		return memberService.findMemberWithKakaoId(openId.sub())
			.map(member -> {
				AuthToken token = authTokenGenerator.generate(String.valueOf(member.getId()));
				return ResponseEntity.ok(new AuthResponse(member.getId(), member.getName(), token));
			})
			.orElseGet(() -> ResponseEntity.ok(new AuthResponse(null, openId.sub(), null)));
	}

	private KakaoOpenIdPayload parseOpenIdToken(String idToken) { // todo: 유틸로 빼기
		try {
			String[] parts = idToken.split("\\.");
			String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
			return JsonMapper.readValue(payload, KakaoOpenIdPayload.class);
		} catch (Exception e) {
			throw new IllegalStateException("카카오 Open Id 토큰 파싱 실패", e);
		}
	}
}
