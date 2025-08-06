package com.memento.server.api.controller.community.dto;

import lombok.Builder;

@Builder
public record UpdateAssociateRequest(
	String profileImageUrl,
	String nickname,
	Long achievement,
	String introduction
) {
}
