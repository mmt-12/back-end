package com.memento.server.unit.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.service.oauth.KakaoJwk;
import com.memento.server.api.service.oauth.KakaoJwks;
import com.memento.server.api.service.oauth.KakaoKeyMemory;
import com.memento.server.client.oauth.KakaoClient;

public class KakaoKeyMemoryTest {

	private KakaoClient kakaoClient;
	private KakaoKeyMemory kakaoKeyMemory;
	private RSAPublicKey rsaPublicKey;

	@BeforeEach
	void setUp() throws Exception {
		kakaoClient = mock(KakaoClient.class);
		kakaoKeyMemory = new KakaoKeyMemory(kakaoClient);

		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		KeyPair keyPair = generator.generateKeyPair();
		rsaPublicKey = (RSAPublicKey)keyPair.getPublic();
	}

	@Test
	@DisplayName("캐시에 없는 kid면 JWKS를 조회해 키를 반환한다")
	void refreshOnUnknownKid() {
		// given
		when(kakaoClient.getJwks()).thenReturn(jwksOf("rotated-kid"));

		// when
		PublicKey publicKey = kakaoKeyMemory.getPublicKeyByKid("rotated-kid");

		// then
		assertThat(publicKey).isEqualTo(rsaPublicKey);
		verify(kakaoClient, times(1)).getJwks();
	}

	@Test
	@DisplayName("캐시된 kid는 JWKS를 다시 조회하지 않는다")
	void useCacheForKnownKid() {
		// given
		when(kakaoClient.getJwks()).thenReturn(jwksOf("cached-kid"));

		// when
		kakaoKeyMemory.getPublicKeyByKid("cached-kid");
		kakaoKeyMemory.getPublicKeyByKid("cached-kid");

		// then
		verify(kakaoClient, times(1)).getJwks();
	}

	@Test
	@DisplayName("JWKS 갱신 후에도 없는 kid면 예외를 던진다")
	void throwOnStillUnknownKid() {
		// given
		when(kakaoClient.getJwks()).thenReturn(jwksOf("known-kid"));

		// when // then
		assertThatThrownBy(() -> kakaoKeyMemory.getPublicKeyByKid("forged-kid"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효하지 않은 kid 입니다.");
	}

	@Test
	@DisplayName("갱신 쿨다운 안에 들어온 미지의 kid는 JWKS를 다시 조회하지 않는다")
	void throttleRefreshWithinCooldown() {
		// given
		when(kakaoClient.getJwks()).thenReturn(jwksOf("known-kid"));

		// when
		assertThatThrownBy(() -> kakaoKeyMemory.getPublicKeyByKid("forged-kid-1"))
			.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> kakaoKeyMemory.getPublicKeyByKid("forged-kid-2"))
			.isInstanceOf(IllegalArgumentException.class);

		// then
		verify(kakaoClient, times(1)).getJwks();
	}

	private KakaoJwks jwksOf(String kid) {
		KakaoJwk jwk = new KakaoJwk(kid, "RSA", "RS256", "sig",
			base64Url(rsaPublicKey.getModulus()),
			base64Url(rsaPublicKey.getPublicExponent()));
		return new KakaoJwks(List.of(jwk));
	}

	private String base64Url(BigInteger value) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(value.toByteArray());
	}
}
