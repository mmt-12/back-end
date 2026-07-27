package com.memento.server.api.service.oauth;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.memento.server.client.oauth.KakaoClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoKeyMemory {

	// Kakao rotates JWKS keys; throttle refreshes so forged kids cannot hammer kauth
	private static final long REFRESH_COOLDOWN_MILLIS = 60_000L;

	private final KakaoClient kakaoClient;
	private final Map<String, PublicKey> kakaoPublicKeys = new ConcurrentHashMap<>();
	private volatile long lastRefreshedAt = 0L;

	public PublicKey getPublicKeyByKid(String kid) {
		PublicKey cached = kakaoPublicKeys.get(kid);
		if (cached != null) {
			return cached;
		}
		refreshKeys();
		return requireCachedKey(kid);
	}

	private synchronized void refreshKeys() {
		if (System.currentTimeMillis() - lastRefreshedAt < REFRESH_COOLDOWN_MILLIS) {
			return;
		}
		kakaoClient.getJwks().keys().forEach(this::cacheKey);
		lastRefreshedAt = System.currentTimeMillis();
	}

	private void cacheKey(KakaoJwk jwk) {
		kakaoPublicKeys.put(jwk.kid(), buildRsaPublicKey(jwk.n(), jwk.e()));
	}

	private PublicKey requireCachedKey(String kid) {
		PublicKey key = kakaoPublicKeys.get(kid);
		if (key == null) {
			throw new IllegalArgumentException("유효하지 않은 kid 입니다.");
		}
		return key;
	}

	private PublicKey buildRsaPublicKey(String n, String e) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
			BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
			RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
			return keyFactory.generatePublic(spec);
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to create RSA public key", ex);
		}
	}
}
