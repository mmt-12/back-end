package com.memento.server.fcm;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.fcm.FCMToken;

public class FCMTokenFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String DEFAULT_TOKEN = "fcm_token_example_1234567890abcdefghijklmnopqrstuvwxyz";

	public static FCMToken fcmToken() {
		return FCMToken.builder()
			.id(idGenerator.getAndIncrement())
			.token(DEFAULT_TOKEN + idGenerator.get())
			.associate(AssociateFixtures.associate())
			.build();
	}

	public static FCMToken fcmToken(Associate associate) {
		return FCMToken.builder()
			.token(DEFAULT_TOKEN + idGenerator.getAndIncrement())
			.associate(associate)
			.build();
	}

	public static FCMToken fcmToken(String token, Associate associate) {
		return FCMToken.builder()
			.token(token)
			.associate(associate)
			.build();
	}
}