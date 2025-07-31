package com.memento.server.api.controller.post.dto.read;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class Emoji extends Reaction {
	boolean isInvolved;

	public static Emoji from(){
		return Emoji.builder()
			.id(1L)
			.url("https://aws.s3.memento/1")
			.isInvolved(true)
			.authors(List.of(CommentAuthor.from()))
			.build();
	}
}
