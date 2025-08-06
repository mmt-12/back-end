package com.memento.server.client.oauth;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.memento.server.api.service.oauth.KakaoToken;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoClient {

	private final KakaoClientProperties kakaoClientProperties;

	private RestClient restClient() {
		return RestClient.builder().build();
	}

	public String getAuthUrl() {
		return UriComponentsBuilder
			.fromUriString(kakaoClientProperties.kauthHost() + "/oauth/authorize")
			.queryParam("client_id", kakaoClientProperties.clientId())
			.queryParam("redirect_uri", kakaoClientProperties.redirectUri())
			.queryParam("response_type", "code")
			.queryParam("scope", "openid")
			.queryParam("prompt", "select_account")
			.build()
			.toUriString();
	}

	public KakaoToken getKakaoToken(String code) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "authorization_code");
		formData.add("client_id", kakaoClientProperties.clientId());
		formData.add("client_secret", kakaoClientProperties.clientSecret());
		formData.add("code", code);
		formData.add("redirect_uri", kakaoClientProperties.redirectUri());

		return restClient().post()
			.uri(kakaoClientProperties.kauthHost() + "/oauth/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(formData)
			.retrieve()
			.body(KakaoToken.class);
	}
}
