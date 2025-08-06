package com.memento.server.api.controller.mbti.dto;

import lombok.Builder;

@Builder
public record CreateMbtiRequest(
	String mbti
) {
}
