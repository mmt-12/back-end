package com.memento.server.api.controller.profileImage.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record ReadProfileImageResponse(
	List<ProfileImage> profileImages,
	long cursor,
	boolean hasNext
) {
	public static ReadProfileImageResponse from() {
		ProfileImage img1 = new ProfileImage(1L, "www.example.com/s3/seonwoo/1");
		ProfileImage img2 = new ProfileImage(2L, "www.example.com/s3/seonwoo/2");

		return new ReadProfileImageResponse(
			List.of(img1, img2),
			2L,
			false
		);
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class ProfileImage {
		Long id;
		String url;
	}
}
