package com.memento.server.domain.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentType {
	EMOJI("이모지"),
	VOICE("보이스");

	private final String displayName;
}