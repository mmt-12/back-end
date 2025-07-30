package com.memento.server.service.auth.jwt;

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
}
