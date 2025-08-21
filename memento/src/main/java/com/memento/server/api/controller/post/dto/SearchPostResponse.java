package com.memento.server.api.controller.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memento.server.api.controller.post.dto.read.Emoji;
import com.memento.server.api.controller.post.dto.read.PostAuthor;
import com.memento.server.api.controller.post.dto.read.TemporaryVoice;
import com.memento.server.api.controller.post.dto.read.Voice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record SearchPostResponse(
	Long id,
	PostAuthor author,
	List<String> pictures,
	String content,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime createdAt,

	Comment comments
) {
	@Getter
	@Builder
	@AllArgsConstructor
	public static class Comment{
		List<Emoji> emojis;
		List<Voice> voices;
		List<TemporaryVoice> temporaryVoices;
	}
}
