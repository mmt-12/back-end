package com.memento.server.service.auth.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final JwtProperties jwtProperties;

	public JwtToken createTempToken(MemberClaim memberClaim) {
		long now = System.currentTimeMillis();
		Date accessTokenExpiresAt = new Date(now + jwtProperties.tempTime());

		String accessToken = JWT.create()
			.withClaim("memberId", memberClaim.memberId())
			.withClaim("associateId", memberClaim.associateId())
			.withClaim("communityId", memberClaim.communityId())
			.withExpiresAt(accessTokenExpiresAt)
			.sign(Algorithm.HMAC512(jwtProperties.secret()));

		return JwtToken.builder()
			.grantType(jwtProperties.grantType())
			.accessToken(accessToken)
			.accessTokenExpiresAt(accessTokenExpiresAt)
			.build();
	}

	public JwtToken createToken(MemberClaim memberClaim) {
		long now = System.currentTimeMillis();
		Date accessTokenExpiresAt = new Date(now + jwtProperties.accessTokenExpireTime());
		Date refreshTokenExpiresAt = new Date(now + jwtProperties.refreshTokenExpireTime());

		String accessToken = JWT.create()
			.withClaim("memberId", memberClaim.memberId())
			.withClaim("associateId", memberClaim.associateId())
			.withClaim("communityId", memberClaim.communityId())
			.withExpiresAt(accessTokenExpiresAt)
			.sign(Algorithm.HMAC512(jwtProperties.secret()));

		String refreshToken = JWT.create()
			.withExpiresAt(refreshTokenExpiresAt)
			.sign(Algorithm.HMAC512(jwtProperties.secret()));

		return JwtToken.builder()
			.grantType(jwtProperties.grantType())
			.accessToken(accessToken)
			.accessTokenExpiresAt(accessTokenExpiresAt)
			.refreshToken(refreshToken)
			.refreshTokenExpiresAt(refreshTokenExpiresAt)
			.build();
	}
}
