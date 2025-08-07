package com.memento.server.api.controller.memory.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record DownloadImagesResponse(
	List<String> pictures
) {
}
