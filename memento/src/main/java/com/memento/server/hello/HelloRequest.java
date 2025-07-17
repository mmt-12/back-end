package com.memento.server.hello;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record HelloRequest(
	@NotNull(message = "id는 필수입니다.")
	Long id,
	@Positive(message = "가격은 필수입니다.")
	Integer price,
	@NotBlank(message = "이름은 필수입니다.")
	String name
) {
}
