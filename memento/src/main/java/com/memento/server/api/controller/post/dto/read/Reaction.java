package com.memento.server.api.controller.post.dto.read;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Reaction {
	Long id;
	String url;
	String name;
	List<CommentAuthor> authors;
}
