package com.memento.server.api.controller.auth.dto;

import com.memento.server.api.service.auth.jwt.JwtToken;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PROTECTED)
public record AuthGuestResponse(
	Long kakaoId,
	String email,
	JwtToken token
) implements AuthResponse {

	public static AuthGuestResponse of(Long kakaoId, String email, JwtToken jwtToken) {
		return AuthGuestResponse.builder()
			.kakaoId(kakaoId)
			.email(email)
			.token(jwtToken)
			.build();
	}
}
