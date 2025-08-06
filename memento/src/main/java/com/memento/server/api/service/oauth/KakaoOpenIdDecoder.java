package com.memento.server.api.service.oauth;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.memento.server.utility.json.JsonMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoOpenIdDecoder {

	private final KakaoKeyMemory kakaoKeyMemory;

	public KakaoOpenIdPayload validateOpenIdToken(String token) {
		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			throw new RuntimeException("jwt 토큰이 아닙니다.");
		}

		String headerString = new String(Base64.getUrlDecoder().decode(parts[0]));
		String payloadString = new String(Base64.getUrlDecoder().decode(parts[1]));
		KakaoOpenIdHeader header = JsonMapper.readValue(headerString, KakaoOpenIdHeader.class);
		KakaoOpenIdPayload payload = JsonMapper.readValue(payloadString, KakaoOpenIdPayload.class);

		PublicKey key = kakaoKeyMemory.getPublicKeyByKid(header.kid());
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey)key, null);
		JWTVerifier verifier = JWT.require(algorithm).build();
		verifier.verify(token);

		return payload;
	}
}
