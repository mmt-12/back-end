package com.memento.server.utility.jwt;

import lombok.Builder;

@Builder
public record JwtToken(
	String header,
	String payload,
	String signature
) {
}
