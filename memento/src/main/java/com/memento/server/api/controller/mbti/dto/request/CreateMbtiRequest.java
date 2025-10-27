package com.memento.server.api.controller.mbti.dto.request;

import com.memento.server.domain.mbti.Mbti;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateMbtiRequest(
	@NotNull(message = "MBTI는 비어 있을 수 없습니다.")
	Mbti mbti
) {
}
