package com.memento.server.api.controller.post.dto.read;

public record Achievement(
	Long id,
	String name
) {

	public static Achievement from() {
		return new Achievement(1L, "뤼전드");
	}
}
