package com.memento.server.api.service.auth.jwt;

import java.util.Date;

import lombok.Builder;

@Builder
public record JwtToken(
	String grantType,
	String accessToken,
	Date accessTokenExpiresAt,
	String refreshToken,
	Date refreshTokenExpiresAt
) {

	public static JwtToken of(JwtProperties jwtProperties, String accessToken, Date accessTokenExpiresAt) {
		return JwtToken.builder()
			.grantType(jwtProperties.grantType())
			.accessToken(accessToken)
			.accessTokenExpiresAt(accessTokenExpiresAt)
			.build();
	}

	public static JwtToken of(JwtProperties jwtProperties, String accessToken, Date accessTokenExpiresAt,
		String refreshToken, Date refreshTokenExpiresAt) {
		return JwtToken.builder()
			.grantType(jwtProperties.grantType())
			.accessToken(accessToken)
			.accessTokenExpiresAt(accessTokenExpiresAt)
			.refreshToken(refreshToken)
			.refreshTokenExpiresAt(refreshTokenExpiresAt)
			.build();
	}
}
