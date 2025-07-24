package com.memento.server.client.oauth;

import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memento.server.controller.auth.AuthResponse;
import com.memento.server.controller.auth.AuthToken;
import com.memento.server.service.auth.AuthTokenGenerator;
import com.memento.server.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoClient {

	private final ObjectMapper objectMapper;
	private final MemberService memberService;
	private final AuthTokenGenerator authTokenGenerator;
	private final KakaoProperties kakaoProperties;

	private RestClient restClient() {
		return RestClient.builder().build();
	}

	public String getAuthUrl() {
		return UriComponentsBuilder
			.fromUriString(kakaoProperties.kauthHost() + "/oauth/authorize")
			.queryParam("client_id", kakaoProperties.clientId())
			.queryParam("redirect_uri", kakaoProperties.redirectUri())
			.queryParam("response_type", "code")
			.queryParam("scope", "openid")
			.build()
			.toUriString();
	}

	public ResponseEntity<AuthResponse> handleAuthorizationCallback(String code) {
		KakaoTokenResponse kakaoToken = getKakaoToken(code);
		KakaoOpenId openId = parseOpenIdToken(kakaoToken.id_token());
		System.out.println(openId);

		return memberService.findMemberWithKakaoId(openId.sub())
			.map(member -> {
				AuthToken token = authTokenGenerator.generate(String.valueOf(member.getId()));
				return ResponseEntity.ok(new AuthResponse(member.getId(), member.getName(), token));
			})
			.orElseGet(() -> ResponseEntity.ok(new AuthResponse(null, openId.sub(), null)));
	}

	private KakaoTokenResponse getKakaoToken(String code) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "authorization_code");
		formData.add("client_id", kakaoProperties.clientId());
		formData.add("client_secret", kakaoProperties.clientSecret());
		formData.add("code", code);
		formData.add("redirect_uri", kakaoProperties.redirectUri());

		return restClient().post()
			.uri(kakaoProperties.kauthHost() + "/oauth/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(formData)
			.retrieve()
			.body(KakaoTokenResponse.class);
	}

	private KakaoOpenId parseOpenIdToken(String idToken) {
		try {
			String[] parts = idToken.split("\\.");
			if (parts.length != 3)
				throw new IllegalArgumentException("Invalid ID token format");
			String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
			return objectMapper.readValue(payload, KakaoOpenId.class);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to parse Kakao OpenID token", e);
		}
	}
}
