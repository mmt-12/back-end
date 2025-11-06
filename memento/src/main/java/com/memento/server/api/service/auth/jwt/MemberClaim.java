package com.memento.server.api.service.auth.jwt;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.member.Member;

import lombok.Builder;

@Builder
public record MemberClaim(
	Long memberId,
	Long associateId,
	Long communityId,
	Boolean isMember
) {
	public static MemberClaim of(Member member, Associate associate) {
		return MemberClaim.builder()
			.memberId(member.getId())
			.associateId(associate.getId())
			.communityId(associate.getCommunity().getId())
			.isMember(true)
			.build();
	}

	public static MemberClaim from(Long kakaoId) {
		return MemberClaim.builder()
			.memberId(kakaoId)
			.isMember(false)
			.build();
	}

	public static MemberClaim from(Long memberId, Long associateId, Long communityId, Boolean isMember) {
		return MemberClaim.builder()
			.memberId(memberId)
			.associateId(associateId)
			.communityId(communityId)
			.isMember(isMember)
			.build();
	}
}
