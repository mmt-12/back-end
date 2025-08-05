package com.memento.server.controller.member;

import lombok.Builder;

@Builder
public record MemberUpdateRequest(
	String name,
	String email
) {
}
