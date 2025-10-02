package com.memento.server.api.controller.fcm.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SaveFCMTokenRequest(
	@NotBlank(message = "token 값은 필수 입니다.")
	@Size(max = 512)
	String token
) {
}
