package com.memento.server.controller.member;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record MemberSignUpRequest(
	String name,
	String email,
	LocalDate birthday
) {
}
