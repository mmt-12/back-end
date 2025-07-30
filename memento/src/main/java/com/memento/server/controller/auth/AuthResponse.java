package com.memento.server.controller.auth;

import com.memento.server.service.auth.jwt.JwtToken;

public record AuthResponse(
	Long memberId,
	String name,
	JwtToken token
) {
}
