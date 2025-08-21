package com.memento.server.api.controller.post.dto.read;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memento.server.domain.community.Associate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class CommentAuthor extends Author{
	Long commentId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime createdAt;

	public static CommentAuthor from(Associate associate) {
		return CommentAuthor.builder()
			.id(associate.getId())
			.nickname(associate.getNickname())
			.imageUrl(associate.getProfileImageUrl())
			.build();
	}
}
