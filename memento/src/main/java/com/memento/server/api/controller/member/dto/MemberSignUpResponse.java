package com.memento.server.api.controller.member.dto;

import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.domain.member.Member;

import lombok.Builder;

@Builder
public record MemberSignUpResponse(
	Long memberId,
	String name,
	JwtToken token
) {
	public static MemberSignUpResponse from(Member member, JwtToken token) {
		return MemberSignUpResponse.builder()
			.memberId(member.getId())
			.name(member.getName())
			.token(token)
			.build();
	}
}
