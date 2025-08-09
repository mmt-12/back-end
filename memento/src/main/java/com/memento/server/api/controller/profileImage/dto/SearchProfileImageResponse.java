package com.memento.server.api.controller.profileImage.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record SearchProfileImageResponse(
	List<ProfileImage> profileImages,
	long cursor,
	boolean hasNext
) {
	@Getter
	@Builder
	@AllArgsConstructor
	public static class ProfileImage {
		Long id;
		String url;
	}
}
