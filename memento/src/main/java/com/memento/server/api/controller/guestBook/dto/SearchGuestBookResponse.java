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
	Long cursor,
	boolean hasNext
) {
	public static SearchGuestBookResponse from() {
		GuestBook gb1 = new GuestBook(101L, GuestBookType.TEXT, "쑤야 처세 함하자!",
			LocalDateTime.of(2024, 6, 21, 10, 30, 0));
		GuestBook gb2 = new GuestBook(102L, GuestBookType.EMOJI, "www.example.com/emojis/smile.png",
			LocalDateTime.of(2024, 6, 21, 10, 45, 0));

		return new SearchGuestBookResponse(List.of(gb1, gb2), 102L, false);
	}

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
