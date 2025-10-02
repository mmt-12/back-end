package com.memento.server.api.controller.member.dto;

import lombok.Builder;

@Builder
public record MemberUpdateRequest(
	String name,
	String email
) {
}
