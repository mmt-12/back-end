package com.memento.server.api.controller.post.dto.read;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class TemporaryVoice extends Reaction{

	public static TemporaryVoice from(){
		return TemporaryVoice.builder()
			.id(1L)
			.url("https://aws.s3.memento/1")
			.authors(List.of(CommentAuthor.from()))
			.build();
	}
}
