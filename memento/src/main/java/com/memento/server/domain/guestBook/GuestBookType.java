package com.memento.server.domain.guestBook;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GuestBookType {
	TEXT("문자"),
	EMOJI("이모지"),
	VOICE("보이스");

	private final String displayName;
}