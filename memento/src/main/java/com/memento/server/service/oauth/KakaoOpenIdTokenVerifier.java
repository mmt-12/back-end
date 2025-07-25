package com.memento.server.service.oauth;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memento.server.client.oauth.KakaoOpenIdHeader;
import com.memento.server.client.oauth.KakaoOpenIdPayload;
import com.memento.server.client.oauth.KakaoProperties;
import com.memento.server.utility.JsonMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoOpenIdTokenVerifier { // todo: 디코더 유틸로 빼기

	private final KakaoProperties kakaoProperties;
	private final ObjectMapper objectMapper;
	private final KakaoKeyMemory kakaoKeyMemory;

	public void verifyOpenIdToken(String token) {
		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			throw new IllegalArgumentException("JWT 형식이 아닙니다.");
		}

		String headerString = new String(Base64.getUrlDecoder().decode(parts[0]));
		String payloadString = new String(Base64.getUrlDecoder().decode(parts[1]));
		KakaoOpenIdHeader header = JsonMapper.readValue(headerString, KakaoOpenIdHeader.class);
		KakaoOpenIdPayload payload = JsonMapper.readValue(payloadString, KakaoOpenIdPayload.class);

		verifyPayload(payload);
		verifySignature(header, token);

	}

	private void verifySignature(KakaoOpenIdHeader header, String token) {
		PublicKey key = kakaoKeyMemory.getPublicKeyByKid(header.kid());
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey)key, null);
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT jwt = verifier.verify(token);
	}

	private void verifyPayload(KakaoOpenIdPayload payload) {
		if (!payload.iss().equals(kakaoProperties.kauthHost())) {
			throw new IllegalArgumentException("토큰 생성자가 다릅니다.");
		}

		if (!payload.aud().equals(kakaoProperties.clientId())) {
			throw new IllegalArgumentException("다른 서비스에서 생성된 토큰입니다.");
		}

		if (payload.exp() * 1000L < System.currentTimeMillis()) {
			throw new IllegalArgumentException("토큰이 만료되었습니다.");
		}
	}
}
