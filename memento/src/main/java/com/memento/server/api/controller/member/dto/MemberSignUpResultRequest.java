package com.memento.server.api.controller.member.dto;

public record MemberSignUpResultRequest(
	Long memberId,
	Boolean isReject
) {
}
