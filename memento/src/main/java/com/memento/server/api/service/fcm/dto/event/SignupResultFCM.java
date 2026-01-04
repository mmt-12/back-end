package com.memento.server.api.service.fcm.dto.event;

import lombok.Builder;

@Builder
public record SignupResultFCM(
	String fcmToken,
	boolean isAccepted
) implements FCMEvent {

	public static SignupResultFCM accepted(String fcmToken) {
		return SignupResultFCM.builder()
			.fcmToken(fcmToken)
			.isAccepted(true)
			.build();
	}

	public static SignupResultFCM rejected(String fcmToken) {
		return SignupResultFCM.builder()
			.fcmToken(fcmToken)
			.isAccepted(false)
			.build();
	}
}
