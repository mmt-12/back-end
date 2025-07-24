package com.memento.server.controller.auth;

public record AuthResponse(
	Long memberId,
	String name,
	AuthToken token
) {
}
