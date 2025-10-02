package com.memento.server.spring.domain.fcm;

import static com.memento.server.common.error.ErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.fcm.FCMToken;

public class FCMTokenTest {

	@Test
	@DisplayName("FCM 토큰을 생성한다.")
	void createFCMToken() {
		// given
		String token = "valid_fcm_token_1234567890";
		Associate associate = AssociateFixtures.associate();

		// when
		FCMToken fcmToken = FCMToken.create(token, associate);

		// then
		assertThat(fcmToken).isNotNull();
		assertThat(fcmToken.getToken()).isEqualTo(token);
		assertThat(fcmToken.getAssociate()).isEqualTo(associate);
	}

	@Test
	@DisplayName("토큰 길이가 4096을 초과하면 예외가 발생한다.")
	void createFCMTokenWithTooLongToken() {
		// given
		String tooLongToken = "a".repeat(513);
		Associate associate = AssociateFixtures.associate();

		// when & then
		assertThatThrownBy(() -> FCMToken.create(tooLongToken, associate))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(FCMTOKEN_TOO_LONG);
	}

	@Test
	@DisplayName("정확히 4096 길이의 토큰은 정상적으로 생성된다.")
	void createFCMTokenWithMaxLength() {
		// given
		String maxLengthToken = "a".repeat(512);
		Associate associate = AssociateFixtures.associate();

		// when
		FCMToken fcmToken = FCMToken.create(maxLengthToken, associate);

		// then
		assertThat(fcmToken).isNotNull();
		assertThat(fcmToken.getToken()).hasSize(512);
	}
}
