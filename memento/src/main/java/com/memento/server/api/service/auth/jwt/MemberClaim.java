package com.memento.server.api.service.auth.jwt;

import com.memento.server.domain.member.Member;

import lombok.Builder;

@Builder
public record MemberClaim(
	Long memberId,
	Long associateId,
	Long communityId
) {
	public static MemberClaim from(Member member) {
		return MemberClaim.builder()
			.memberId(member.getId())
			.build();
	}
}
