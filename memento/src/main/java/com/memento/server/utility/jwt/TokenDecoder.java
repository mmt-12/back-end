package com.memento.server.utility.jwt;

import java.util.Base64;

public class TokenDecoder {

	public static JwtToken parse(String token) {
		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			throw new IllegalArgumentException("JWT 형식이 아닙니다.");
		}

		return JwtToken.builder()
			.header(parts[0])
			.payload(parts[1])
			.signature(parts[2])
			.build();
	}

	public static String decode(String value) {
		return new String(Base64.getUrlDecoder().decode(value));
	}
}
