package com.memento.server.api.service.emoji.dto.response;

import java.util.List;

import com.memento.server.common.dto.response.PageInfo;

import lombok.Builder;

@Builder
public record EmojiListResponse(
	List<EmojiResponse> emoji,
	PageInfo pageInfo
) {

	public static EmojiListResponse of(List<EmojiResponse> emoji, PageInfo pageInfo) {
		return EmojiListResponse.builder()
			.emoji(emoji)
			.pageInfo(pageInfo)
			.build();
	}
}
