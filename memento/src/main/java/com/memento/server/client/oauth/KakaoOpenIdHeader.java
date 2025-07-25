package com.memento.server.client.oauth;

public record KakaoOpenIdHeader(
	String kid,
	String typ,
	String alg
) {
}
