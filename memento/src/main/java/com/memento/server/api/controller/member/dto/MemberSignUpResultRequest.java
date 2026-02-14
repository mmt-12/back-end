package com.memento.server.api.controller.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberSignUpResultRequest(
	@NotNull Long memberId,
	@NotBlank String token,
	@NotBlank String action
) {
	public boolean isReject() {
		return "reject".equalsIgnoreCase(action);
	}
}
