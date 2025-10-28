package com.memento.server.api.service.auth.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.memento.server.utility.json.JsonMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			.withClaim("isMember", memberClaim.isMember())
			.withExpiresAt(accessTokenExpiresAt)
			.sign(Algorithm.HMAC512(jwtProperties.secret()));

		return JwtToken.of(jwtProperties, accessToken, accessTokenExpiresAt);
	}

	public JwtToken createToken(MemberClaim memberClaim) {
		long now = System.currentTimeMillis();
		Date accessTokenExpiresAt = new Date(now + jwtProperties.accessTokenExpireTime());
		Date refreshTokenExpiresAt = new Date(now + jwtProperties.refreshTokenExpireTime());

		String accessToken = JWT.create()
			.withClaim("memberId", memberClaim.memberId())
			.withClaim("associateId", memberClaim.associateId())
			.withClaim("communityId", memberClaim.communityId())
			.withClaim("isMember", memberClaim.isMember())
			.withExpiresAt(accessTokenExpiresAt)
			.sign(Algorithm.HMAC512(jwtProperties.secret()));

		String refreshToken = JWT.create()
			.withClaim("memberId", memberClaim.memberId())
			.withExpiresAt(refreshTokenExpiresAt)
			.sign(Algorithm.HMAC512(jwtProperties.secret()));

		return JwtToken.of(jwtProperties, accessToken, accessTokenExpiresAt, refreshToken, refreshTokenExpiresAt);
	}

	public boolean isNotValidateToken(String token) {
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtProperties.secret())).acceptLeeway(5).build();
			verifier.verify(token);
			return false;
		} catch (JWTVerificationException e) {
			log.info("토큰 검증 실패: {}", e.getMessage());
			return true;
		}
	}

	public MemberClaim extractMemberClaim(String token) {
		String payloadString = JWT.decode(token).getPayload();
		String payload = new String(Base64.getUrlDecoder().decode(payloadString));
		Map map = JsonMapper.readValue(payload, Map.class);
		return MemberClaim.from(
			map.get("memberId") != null ? ((Number)map.get("memberId")).longValue() : null,
			map.get("associateId") != null ? ((Number)map.get("associateId")).longValue() : null,
			map.get("communityId") != null ? ((Number)map.get("communityId")).longValue() : null,
			map.get("isMember") != null && (Boolean)map.get("isMember")
		);
	}
}
