package com.memento.server.api.controller.auth.dto;

public record TokenRefreshRequest(
	String refreshToken
) {
}
