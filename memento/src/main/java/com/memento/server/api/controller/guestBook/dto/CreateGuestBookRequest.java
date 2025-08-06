package com.memento.server.api.controller.guestBook.dto;

import com.memento.server.domain.guestBook.GuestBookType;

import lombok.Builder;

@Builder
public record CreateGuestBookRequest(
	GuestBookType type,
	Long contentId,
	String content
) {
}
