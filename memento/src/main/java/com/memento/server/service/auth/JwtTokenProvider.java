package com.memento.server.service.auth;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JwtTokenProvider { // todo: 유틸로 빼기

	@Value("${jwt.secret}")
	private String secretKey;

	public String accessTokenGenerate(String subject, Date expiredAt) {
		return JWT.create()
			.withSubject(subject)
			.withExpiresAt(expiredAt)
			.sign(Algorithm.HMAC512(secretKey));
	}

	public String refreshTokenGenerate(Date expiredAt) {
		return JWT.create()
			.withExpiresAt(expiredAt)
			.sign(Algorithm.HMAC512(secretKey));
	}
}
