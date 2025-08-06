package com.memento.server.api.service.oauth;

public record KakaoOpenIdHeader(
	String kid,
	String typ,
	String alg
) {
}
