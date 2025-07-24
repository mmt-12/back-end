package com.memento.server.client.oauth;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
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
import com.memento.server.domain.member.Member;
import com.memento.server.service.auth.AuthTokenGenerator;
import com.memento.server.service.member.MemberService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoClient {

	@Value("${kakao.client-id}")
	private String clientId;

	@Value("${kakao.client-secret}")
	private String clientSecret;

	@Value("${kakao.kauth-host}")
	private String kauthHost;

	@Value("${kakao.kapi-host}")
	private String kapiHost;

	@Value("${kakao.redirect-uri}")
	private String redirectUri;

	private final ObjectMapper objectMapper;
	private final MemberService memberService;
	private final AuthTokenGenerator authTokenGenerator;

	public String getAuthUrl() {
		return UriComponentsBuilder
			.fromUriString(kauthHost + "/oauth/authorize")
			.queryParam("client_id", clientId)
			.queryParam("redirect_uri", redirectUri)
			.queryParam("response_type", "code")
			.queryParam("scope", "openid")
			.build()
			.toUriString();
	}

	public ResponseEntity<AuthResponse> handleAuthorizationCallback(String code) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "authorization_code");
		formData.add("client_id", clientId);
		formData.add("client_secret", clientSecret);
		formData.add("code", code);
		formData.add("redirect_uri", redirectUri);

		RestClient restClient = RestClient.builder().baseUrl(kapiHost).build();
		KakaoTokenResponse tokenResponse = restClient.post()
			.uri(kauthHost + "/oauth/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(formData)
			.retrieve()
			.body(KakaoTokenResponse.class);

		KakaoOpenId openId;
		try {
			String idToken = tokenResponse.id_token();
			String[] parts = idToken.split("\\.");
			openId = objectMapper.readValue(Base64.getUrlDecoder().decode(parts[1]), KakaoOpenId.class);
		} catch (Exception e) {
			throw new RuntimeException();
		}

		Optional<Member> findMember = memberService.findMemberWithKakaoId(openId.sub());
		if (findMember.isEmpty()) {
			return ResponseEntity.ok(new AuthResponse(null, openId.sub(), null));
		}

		Member member = findMember.get();
		AuthToken token = authTokenGenerator.generate(String.valueOf(member.getId()));
		return ResponseEntity.ok(new AuthResponse(member.getId(), member.getName(), token));
	}
}
