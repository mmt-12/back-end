package com.memento.server.api.controller.post.dto.response;

import com.memento.server.api.controller.achievement.dto.response.AchievementResponse;
import com.memento.server.api.service.memory.dto.Author;
import com.memento.server.api.service.memory.dto.MemoryItem;

import lombok.Builder;

@Builder
public record AuthorResponse(
	Long id,
	String imageUrl,
	String nickname,
	AchievementResponse achievement
) {
	public static AuthorResponse of(MemoryItem.AssociateDto associate, MemoryItem.AchievementDto achievement) {
		return AuthorResponse.builder()
			.id(associate.id())
			.imageUrl(associate.profileImageUrl())
			.nickname(associate.nickname())
			.achievement(AchievementResponse.from(achievement))
			.build();
	}

	public static AuthorResponse of(Author author) {
		return AuthorResponse.builder()
			.id(author.id())
			.imageUrl(author.imageUrl())
			.nickname(author.nickname())
			.achievement(AchievementResponse.from(author.achievement()))
			.build();
	}
}
