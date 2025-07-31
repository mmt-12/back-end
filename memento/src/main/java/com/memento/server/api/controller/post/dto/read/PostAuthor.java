package com.memento.server.api.controller.post.dto.read;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class PostAuthor extends Author{

	public static PostAuthor from() {
		return PostAuthor.builder()
			.id(1L)
			.nickname("이중혁")
			.imageUrl("https://aws.s3.memento/1")
			.achievement(Achievement.from())
			.build();
	}
}
