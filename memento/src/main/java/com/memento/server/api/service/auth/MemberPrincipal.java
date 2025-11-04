package com.memento.server.api.service.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.memento.server.api.service.auth.jwt.MemberClaim;

import lombok.Builder;

@Builder
public record MemberPrincipal(
	Long memberId,
	Long associateId,
	Long communityId
) implements UserDetails {

	@Override
	public String getUsername() {
		return String.valueOf(memberId);
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	public static MemberPrincipal from(MemberClaim memberClaim) {
		return MemberPrincipal.builder()
			.memberId(memberClaim.memberId())
			.associateId(memberClaim.associateId())
			.communityId(memberClaim.communityId())
			.build();
	}
}
