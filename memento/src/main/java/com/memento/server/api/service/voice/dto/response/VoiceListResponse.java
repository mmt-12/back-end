package com.memento.server.api.service.voice.dto.response;

import java.util.List;

import com.memento.server.common.dto.response.PageInfo;

import lombok.Builder;

@Builder
public record VoiceListResponse(
	List<VoiceResponse> voices,
	PageInfo pageInfo
) {

	public static VoiceListResponse of(List<VoiceResponse> voices, PageInfo pageInfo) {
		return VoiceListResponse.builder()
			.voices(voices)
			.pageInfo(pageInfo)
			.build();
	}
}
