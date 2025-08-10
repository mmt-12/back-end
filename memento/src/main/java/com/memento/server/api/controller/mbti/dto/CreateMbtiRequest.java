package com.memento.server.api.controller.mbti.dto;

import com.memento.server.domain.mbti.Mbti;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateMbtiRequest(
	@NotBlank(message = "MBTI는 비어 있을 수 없습니다.")
	@Size(min = 4, max = 4, message = "MBTI는 정확히 4자리여야 합니다.")
	String mbti
) {
}
