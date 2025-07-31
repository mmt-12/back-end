package com.memento.server.api.controller.post.dto.read;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Author {
	Long id;
	String imageUrl;
	String nickname;
	Achievement achievement;
}
