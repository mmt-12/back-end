package com.memento.server.api.service.auth.jwt;

import lombok.Builder;

@Builder
public record MemberClaim(
	Long memberId,
	Long associateId,
	Long communityId
) {
}
