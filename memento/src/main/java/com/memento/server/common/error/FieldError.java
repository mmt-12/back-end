package com.memento.server.common.error;

import lombok.Builder;

@Builder
public record FieldError(
	String field,
	String message
) {

	public static FieldError of(String field, String message) {
		return FieldError.builder().field(field).message(message).build();
	}
}
