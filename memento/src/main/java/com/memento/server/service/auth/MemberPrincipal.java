package com.memento.server.service.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record MemberPrincipal(
	Long memberId,
	Long associateId,
	Long communityId) implements UserDetails {

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
}
