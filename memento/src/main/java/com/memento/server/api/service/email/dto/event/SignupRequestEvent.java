package com.memento.server.api.service.email.dto.event;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record SignupRequestEvent(
	Long memberId,
	String name,
	String email,
	LocalDate birthday
) implements EmailEvent {

	public static SignupRequestEvent of(Long memberId, String name, String email, LocalDate birthday) {
		return SignupRequestEvent.builder()
			.memberId(memberId)
			.name(name)
			.email(email)
			.birthday(birthday)
			.build();
	}
}
