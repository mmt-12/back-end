package com.memento.server.service.oauth;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.memento.server.client.oauth.KakaoOpenIdHeader;
import com.memento.server.client.oauth.KakaoOpenIdPayload;
import com.memento.server.client.oauth.KakaoProperties;
import com.memento.server.utility.json.JsonMapper;
import com.memento.server.utility.jwt.JwtToken;
import com.memento.server.utility.jwt.TokenDecoder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoOpenIdDecoder {

	private final KakaoProperties kakaoProperties;
	private final KakaoKeyMemory kakaoKeyMemory;

	public void verifyOpenIdToken(String token) {
		JwtToken parsedToken = TokenDecoder.parse(token);

		String headerString = TokenDecoder.decode(parsedToken.header());
		String payloadString = TokenDecoder.decode(parsedToken.payload());

		KakaoOpenIdHeader header = JsonMapper.readValue(headerString, KakaoOpenIdHeader.class);
		KakaoOpenIdPayload payload = JsonMapper.readValue(payloadString, KakaoOpenIdPayload.class);

		verifyPayload(payload);
		verifySignature(header, token);
	}

	private void verifySignature(KakaoOpenIdHeader header, String token) {
		PublicKey key = kakaoKeyMemory.getPublicKeyByKid(header.kid());
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey)key, null);
		JWTVerifier verifier = JWT.require(algorithm).build();
		verifier.verify(token);
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
