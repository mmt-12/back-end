package com.memento.server.service.auth;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.memento.server.controller.auth.AuthToken;
import com.memento.server.utility.jwt.TokenProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthTokenGenerator {

	private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일
	private final TokenProvider tokenProvider;

	public AuthToken generate(String uid) {
		long now = (new Date()).getTime();
		Date accessTokenExpiredAt = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
		Date refreshTokenExpiredAt = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

		String accessToken = tokenProvider.accessTokenGenerate(uid, accessTokenExpiredAt);
		String refreshToken = tokenProvider.refreshTokenGenerate(refreshTokenExpiredAt);

		return AuthToken.of(accessToken, refreshToken);
	}
}
