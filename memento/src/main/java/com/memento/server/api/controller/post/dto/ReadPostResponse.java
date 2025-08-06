package com.memento.server.api.controller.post.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public record ReadPostResponse(
	Long id,
	PostAuthor author,
	List<String> pictures,
	String content,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime createdAt,

	Comment comments
) {

	public static ReadPostResponse from(){
		return ReadPostResponse.builder()
			.id(1L)
			.author(PostAuthor.from())
			.pictures(List.of("https://aws.s3.memento/1"))
			.content("술에 취한 경완이형은 대단하다!")
			.comments(Comment.from())
			.createdAt(LocalDateTime.of(2024,06,24,10,55,00))
			.build();
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class Comment{
		List<Emoji> emojis;
		List<Voice> voices;
		List<TemporaryVoice> temporaryVoices;

		public static Comment from(){
			return Comment.builder()
				.emojis(List.of(Emoji.from()))
				.voices(List.of(Voice.from()))
				.temporaryVoices(List.of(TemporaryVoice.from()))
				.build();
		}
	}
}
