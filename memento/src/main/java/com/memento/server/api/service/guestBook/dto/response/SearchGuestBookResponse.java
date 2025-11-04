package com.memento.server.api.service.guestBook.dto.response;

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
		String name;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		LocalDateTime createdAt;

		public static GuestBook type(com.memento.server.domain.guestBook.GuestBook guestBook){
			return GuestBook.builder()
				.id(guestBook.getId())
				.type(guestBook.getType())
				.content(guestBook.getContent())
				.name(guestBook.getName())
				.createdAt(guestBook.getCreatedAt())
				.build();
		}
	}

	public static SearchGuestBookResponse of(List<GuestBook> guestBooks, Long nextCursor, boolean hasNext){
		return SearchGuestBookResponse.builder()
			.guestBooks(guestBooks)
			.nextCursor(nextCursor)
			.hasNext(hasNext)
			.build();
	}
}
