package com.memento.server.api.service.email.dto.event;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record SignupRequestEvent(
	Long memberId,
	String name,
	String email,
	LocalDate birthday,
	String token
) implements EmailEvent {

	public static SignupRequestEvent of(Long memberId, String name, String email, LocalDate birthday, String token) {
		return SignupRequestEvent.builder()
			.memberId(memberId)
			.name(name)
			.email(email)
			.birthday(birthday)
			.token(token)
			.build();
	}
}
