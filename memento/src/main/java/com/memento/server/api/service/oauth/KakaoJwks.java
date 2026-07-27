package com.memento.server.api.service.oauth;

import java.util.List;

public record KakaoJwks(
	List<KakaoJwk> keys
) {
}
