package com.memento.server.api.controller.auth.dto;

import com.memento.server.api.service.auth.jwt.JwtToken;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PROTECTED)
public record AuthMemberResponse(
	Long memberId,
	String name,
	JwtToken token
) implements AuthResponse {

	public static AuthMemberResponse of(Long memberId, String name, JwtToken token) {
		return AuthMemberResponse.builder()
			.memberId(memberId)
			.name(name)
			.token(token)
			.build();
	}
}
