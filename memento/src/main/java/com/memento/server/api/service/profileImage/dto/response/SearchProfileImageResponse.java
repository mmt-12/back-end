package com.memento.server.api.service.profileImage.dto.response;

import java.util.List;
import java.util.Objects;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.profileImage.ProfileImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record SearchProfileImageResponse(
	List<ProfileImage> profileImages,
	Long nextCursor,
	boolean hasNext
) {
	@Getter
	@Builder
	@AllArgsConstructor
	public static class ProfileImage {
		Long id;
		String url;
		boolean isRegister;

		public static ProfileImage type(com.memento.server.domain.profileImage.ProfileImage profileImage, Associate associate) {
			return SearchProfileImageResponse.ProfileImage.builder()
				.id(profileImage.getId())
				.url(profileImage.getUrl())
				.isRegister(Objects.equals(profileImage.getRegistrant().getId(), associate.getId()))
				.build();
		}
	}

	public static SearchProfileImageResponse from(List<ProfileImage> profileImages, Long lastCursor, boolean hasNext) {
		return SearchProfileImageResponse.builder()
			.profileImages(profileImages)
			.nextCursor(lastCursor)
			.hasNext(hasNext)
			.build();
	}
}
