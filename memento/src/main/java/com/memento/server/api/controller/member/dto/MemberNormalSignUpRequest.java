package com.memento.server.api.controller.member.dto;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record MemberNormalSignUpRequest(
	String name,
	String email,
	String password,
	LocalDate birthday,
	String secret
) {
}
