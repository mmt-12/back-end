package com.memento.server.api.service.oauth;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class KakaoKeyMemory { // todo: kauth 요청 및 캐싱으로 가져오기

	private final Map<String, PublicKey> kakaoPublicKeys = new HashMap<>();

	public KakaoKeyMemory() {
		kakaoPublicKeys.put("9f252dadd5f233f93d2fa528d12fea",
			buildRsaPublicKey(
				"qGWf6RVzV2pM8YqJ6by5exoixIlTvdXDfYj2v7E6xkoYmesAjp_1IYL7rzhpUYqIkWX0P4wOwAsg-Ud8PcMHggfwUNPOcqgSk1hAIHr63zSlG8xatQb17q9LrWny2HWkUVEU30PxxHsLcuzmfhbRx8kOrNfJEirIuqSyWF_OBHeEgBgYjydd_c8vPo7IiH-pijZn4ZouPsEg7wtdIX3-0ZcXXDbFkaDaqClfqmVCLNBhg3DKYDQOoyWXrpFKUXUFuk2FTCqWaQJ0GniO4p_ppkYIf4zhlwUYfXZEhm8cBo6H2EgukntDbTgnoha8kNunTPekxWTDhE5wGAt6YpT4Yw",
				"AQAB"
			));

		kakaoPublicKeys.put("3f96980381e451efad0d2ddd30e3d3",
			buildRsaPublicKey(
				"q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw",
				"AQAB"
			));
	}

	public PublicKey getPublicKeyByKid(String kid) {
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
