package com.memento.server.api.service.oauth;

public record KakaoJwk(
	String kid,
	String kty,
	String alg,
	String use,
	String n,
	String e
) {
}
