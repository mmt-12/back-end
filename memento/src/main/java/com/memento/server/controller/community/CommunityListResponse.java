package com.memento.server.controller.community;

import java.util.List;

import lombok.Builder;

@Builder
public record CommunityListResponse(
	List<CommunityResponse> communities
) {

	@Builder
	public record CommunityResponse(
		Long id,
		String name,
		Long associateId
	) {
	}
}
