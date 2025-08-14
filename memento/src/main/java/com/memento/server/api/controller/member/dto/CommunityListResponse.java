package com.memento.server.api.controller.member.dto;

import java.util.ArrayList;
import java.util.List;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;

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

	public static CommunityListResponse from(List<Associate> associates) {
		List<CommunityResponse> communityList = new ArrayList<>();
		for (Associate associate : associates) {
			Community community = associate.getCommunity();

			communityList.add(
				CommunityResponse.builder()
					.id(community.getId())
					.name(community.getName())
					.associateId(associate.getId())
					.build()
			);
		}

		return CommunityListResponse.builder()
			.communities(communityList)
			.build();
	}
}
