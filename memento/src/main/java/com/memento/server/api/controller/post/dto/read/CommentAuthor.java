package com.memento.server.api.controller.post.dto.read;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class CommentAuthor extends Author{
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime createdAt;

	public static CommentAuthor from() {
		return CommentAuthor.builder()
			.id(1L)
			.nickname("이중혁")
			.imageUrl("https://aws.s3.memento/1")
			.achievement(Achievement.from())
			.createdAt(LocalDateTime.of(2025,07,12,10,30,00))
			.build();
	}
}
