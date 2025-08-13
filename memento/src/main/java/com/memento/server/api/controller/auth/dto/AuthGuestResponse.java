package com.memento.server.api.controller.auth.dto;

import com.memento.server.api.service.auth.jwt.JwtToken;

import lombok.Builder;

@Builder
public record AuthGuestResponse(
	Long kakaoId,
	String email,
	JwtToken token
) implements AuthResponse {
}
