package com.memento.server.api.controller.community.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateAssociateRequest(
	@Size(max = 255, message = "url의 길이는 최대 255입니다.")
	String profileImageUrl,

	@Size(min = 1, max = 51, message = "nickname의 길이는 최대 51입니다.")
	String nickname,
	Long achievement,

	@Size(max = 255, message = "introduction의 길이는 최대 255입니다.")
	String introduction
) {
}
