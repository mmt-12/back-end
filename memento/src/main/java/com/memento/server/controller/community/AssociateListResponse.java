package com.memento.server.controller.community;

import java.util.List;

import lombok.Builder;

@Builder
public record AssociateListResponse(
	String communityName,
	List<AssociateResponse> associates,
	Long cursor,
	Boolean hasNext
) {

	@Builder
	public record AssociateResponse(
		Long id,
		String nickname,
		String imageUrl,
		String introduction,
		AchievementResponse achievement
	) {

		@Builder
		public record AchievementResponse(
			Long id,
			String name
		) {
		}
	}
}
