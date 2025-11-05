package com.memento.server.api.controller.memory.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.memento.server.domain.post.PostImage;

import lombok.Builder;

@Builder
public record DownloadImagesResponse(
	List<String> pictures
) {

	public static DownloadImagesResponse from(List<PostImage> postImages) {
		List<String> urls = new ArrayList<>();
		for (PostImage postImage : postImages) {
			urls.add(postImage.getUrl());
		}
		return DownloadImagesResponse.builder()
			.pictures(urls)
			.build();
	}
}
