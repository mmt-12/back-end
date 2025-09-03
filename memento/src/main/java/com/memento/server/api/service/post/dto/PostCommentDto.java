package com.memento.server.api.service.post.dto;

import com.memento.server.domain.comment.CommentType;
import com.memento.server.domain.community.Associate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostCommentDto {
	private Long id;
	private Long postId;
	private Associate associate;
	private String url;
	private CommentType type;
	private String name;
	private Boolean isTemporary;
}
