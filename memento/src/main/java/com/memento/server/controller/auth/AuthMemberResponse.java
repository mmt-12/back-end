package com.memento.server.controller.auth;

import com.memento.server.service.auth.jwt.JwtToken;

import lombok.Builder;

@Builder
public record AuthMemberResponse(
	Long memberId,
	String name,
	JwtToken token
) implements AuthResponse {
}
