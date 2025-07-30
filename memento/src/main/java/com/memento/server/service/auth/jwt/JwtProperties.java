package com.memento.server.service.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
	String grantType,
	String secret,
	Long accessTokenExpireTime,
	Long refreshTokenExpireTime
) {
}
