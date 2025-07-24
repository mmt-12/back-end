package com.memento.server.controller.auth;

public record AuthToken(
	String accessToken,
	String refreshToken,
	String grantType
) {
	public static AuthToken of(String accessToken, String refreshToken, String bearerType) {
		return new AuthToken(accessToken, refreshToken, bearerType);
	}
}
