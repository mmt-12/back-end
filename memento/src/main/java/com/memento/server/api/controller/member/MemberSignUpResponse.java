package com.memento.server.api.controller.member;

import com.memento.server.api.service.auth.jwt.JwtToken;

import lombok.Builder;

@Builder
public record MemberSignUpResponse(
	Long memberId,
	String name,
	JwtToken token
) {
}
