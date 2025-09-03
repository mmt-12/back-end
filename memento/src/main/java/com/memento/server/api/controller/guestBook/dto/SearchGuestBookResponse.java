package com.memento.server.api.controller.guestBook.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memento.server.domain.guestBook.GuestBookType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record SearchGuestBookResponse(
	List<GuestBook> guestBooks,
	Long nextCursor,
	boolean hasNext
) {
	@Getter
	@Builder
	@AllArgsConstructor
	public static class GuestBook {
		Long id;
		GuestBookType type;
		String content;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		LocalDateTime createdAt;
	}
}
