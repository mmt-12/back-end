package com.memento.server.service.auth.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.IncorrectClaimException;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.TokenExpiredException;
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

	public boolean validateToken(String token) {
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC512(jwtProperties.secret())).build();
			verifier.verify(token);
			return true;
		} catch (TokenExpiredException e) {
			log.info("만료된 토큰입니다.");
		} catch (MissingClaimException e) {
			log.info("토큰에 누락이 있습니다.");
		} catch (IncorrectClaimException e) {
			log.info("토큰에 잘못된 값이 있습니다.");
		} catch (Exception e) {
			log.info("토큰 검증 오류 {}", e);
		}
		return false;
	}

	public MemberClaim extractMemberClaim(String token) { // todo: 개선하기
		String payloadString = JWT.decode(token).getPayload();
		String payload = new String(Base64.getUrlDecoder().decode(payloadString));
		Map map = JsonMapper.readValue(payload, Map.class);
		return MemberClaim.builder()
			.memberId(map.get("memberId") != null ? ((Number)map.get("memberId")).longValue() : null)
			.associateId(map.get("associateId") != null ? ((Number)map.get("associateId")).longValue() : null)
			.communityId(map.get("communityId") != null ? ((Number)map.get("communityId")).longValue() : null)
			.build();
	}
}
