package com.memento.server.api.service.post.dto.response.search;

import java.util.List;

import com.memento.server.api.service.post.dto.PostCommentDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class TemporaryVoice extends Reaction{

	public static TemporaryVoice of(List<PostCommentDto> dtoList, String url, List<CommentAuthor> authors){
		return TemporaryVoice.builder()
			.id(dtoList.get(0).getReactionId())
			.url(url)
			.name(dtoList.get(0).getName())
			.authors(authors)
			.count(dtoList.size())
			.build();
	}
}
