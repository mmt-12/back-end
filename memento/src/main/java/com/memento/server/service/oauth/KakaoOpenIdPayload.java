package com.memento.server.service.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoOpenIdPayload(
	String iss,
	String aud,
	String sub,
	Integer iat,
	Integer exp,
	@JsonProperty("auth_time") Integer authTime
) {
}
