package com.memento.server.api.service.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memento.server.api.service.post.dto.response.search.Achievement;
import com.memento.server.api.service.post.dto.response.search.Emoji;
import com.memento.server.api.service.post.dto.response.search.PostAuthor;
import com.memento.server.api.service.post.dto.response.search.TemporaryVoice;
import com.memento.server.api.service.post.dto.response.search.Voice;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record SearchPostResponse(
	Long id,
	PostAuthor author,
	List<String> pictures,
	String content,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	LocalDateTime createdAt,

	Comment comments
) {
	@Getter
	@Builder
	@AllArgsConstructor
	public static class Comment{
		List<Emoji> emojis;
		List<Voice> voices;
		List<TemporaryVoice> temporaryVoices;

		public static Comment of(List<Emoji> emojis, List<Voice> voices, List<TemporaryVoice> temporaryVoices){
			return Comment.builder()
				.emojis(emojis)
				.voices(voices)
				.temporaryVoices(temporaryVoices)
				.build();
		}
	}

	public static SearchPostResponse of(Post post, List<PostImage> images, Comment commentsResponse){
		return SearchPostResponse.builder()
			.id(post.getId())
			.createdAt(post.getCreatedAt())
			.author(PostAuthor.builder()
				.id(post.getAssociate().getId())
				.imageUrl(post.getAssociate().getProfileImageUrl())
				.nickname(post.getAssociate().getNickname())
				.achievement(post.getAssociate().getAchievement() == null ? null :
					Achievement.builder()
						.id(post.getAssociate().getAchievement().getId())
						.name(post.getAssociate().getAchievement().getName())
						.build())
				.build())
			.content(post.getContent())
			.pictures(images.stream().map(PostImage::getUrl).toList())
			.comments(commentsResponse)
			.build();
	}
}
