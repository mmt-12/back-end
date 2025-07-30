package com.memento.server.client.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao")
public record KakaoClientProperties(
	String clientId,
	String clientSecret,
	String kauthHost,
	String kapiHost,
	String redirectUri
) {
}
