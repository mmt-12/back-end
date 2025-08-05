package com.memento.server.controller.auth;

import com.memento.server.service.auth.jwt.JwtToken;

import lombok.Builder;

@Builder
public record AuthGuestResponse(
	Long kakaoId,
	String email,
	JwtToken token
) implements AuthResponse {
}
