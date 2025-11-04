package com.memento.server.api.service.community.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.community.Associate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record SearchAssociateResponse(
	String nickname,
	Achievement achievement,
	String imageUrl,
	String introduction,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate birthday
) {
	@Getter
	@Builder
	@AllArgsConstructor
	public static class Achievement {
		Long id;
		String name;

		public static Achievement of(com.memento.server.domain.achievement.Achievement achievement){
			return Achievement.builder()
				.id(achievement.getId())
				.name(achievement.getName())
				.build();
		}
	}

	public static SearchAssociateResponse of(Associate associate, com.memento.server.domain.achievement.Achievement achievement){
		return SearchAssociateResponse.builder()
			.nickname(associate.getNickname())
			.achievement(achievement != null ?
				SearchAssociateResponse.Achievement.of(achievement) : null)
			.imageUrl(associate.getProfileImageUrl())
			.introduction(associate.getIntroduction())
			.birthday(associate.getMember().getBirthday())
			.build();
	}
}
