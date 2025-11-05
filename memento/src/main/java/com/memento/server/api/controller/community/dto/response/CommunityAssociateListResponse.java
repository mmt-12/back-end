package com.memento.server.api.controller.community.dto.response;

import java.util.List;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;

import lombok.Builder;

@Builder
public record CommunityAssociateListResponse(
	String communityName,
	List<AssociateResponse> associates
) {

	public static CommunityAssociateListResponse from(List<Associate> associates, Community community) {
		return CommunityAssociateListResponse.builder()
			.communityName(community.getName())
			.associates(AssociateResponse.from(associates))
			.build();
	}
}
