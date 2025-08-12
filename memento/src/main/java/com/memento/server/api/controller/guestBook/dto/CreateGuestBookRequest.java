package com.memento.server.api.controller.guestBook.dto;

import com.memento.server.domain.guestBook.GuestBookType;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateGuestBookRequest(
	GuestBookType type,
	Long contentId,

	@Size(min = 1, max = 255, message = "content는 최대 크기가 255입니다.")
	String content
) {
}
