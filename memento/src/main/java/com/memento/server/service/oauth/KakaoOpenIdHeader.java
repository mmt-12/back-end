package com.memento.server.service.oauth;

public record KakaoOpenIdHeader(
	String kid,
	String typ,
	String alg
) {
}
