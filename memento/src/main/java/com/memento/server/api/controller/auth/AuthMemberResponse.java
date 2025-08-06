package com.memento.server.api.controller.auth;

import com.memento.server.api.service.auth.jwt.JwtToken;

import lombok.Builder;

@Builder
public record AuthMemberResponse(
	Long memberId,
	String name,
	JwtToken token
) implements AuthResponse {
}
