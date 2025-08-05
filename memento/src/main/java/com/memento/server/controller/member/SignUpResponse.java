package com.memento.server.controller.member;

import com.memento.server.service.auth.jwt.JwtToken;

import lombok.Builder;

@Builder
public record SignUpResponse(
	Long memberId,
	String name,
	JwtToken token
) {
}
