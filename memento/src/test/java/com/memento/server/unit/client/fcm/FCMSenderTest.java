package com.memento.server.unit.client.fcm;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.memento.server.client.fcm.FCMSender;

public class FCMSenderTest {

	private final FCMSender fcmSender = new FCMSender();

	@Test
	@DisplayName("FCM 메시지를 성공적으로 전송한다")
	void sendMessage() throws FirebaseMessagingException {
		// given
		String token = "test-fcm-token-123";
		String title = "테스트 제목";
		String content = "테스트 내용";
		Map<String, String> data = Map.of("key1", "value1", "key2", "value2");

		FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
		when(mockMessaging.send(any(Message.class))).thenReturn("message-id-123");

		// when
		try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
			mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);

			fcmSender.send(token, title, content, data);

			// then
			verify(mockMessaging, times(1)).send(any(Message.class));
		}
	}

	@Test
	@DisplayName("Firebase 전송 실패 시 예외를 던진다 - INVALID_ARGUMENT")
	void sendMessageThrowsInvalidArgumentException() throws FirebaseMessagingException {
		// given
		String token = "invalid-token";
		String title = "실패 테스트";
		String content = "전송 실패";
		Map<String, String> data = Map.of("test", "data");

		FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
		FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
		when(exception.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INVALID_ARGUMENT);
		when(mockMessaging.send(any(Message.class))).thenThrow(exception);

		// when & then
		try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
			mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);

			assertThatThrownBy(() -> fcmSender.send(token, title, content, data))
				.isInstanceOf(FirebaseMessagingException.class)
				.hasFieldOrPropertyWithValue("messagingErrorCode", MessagingErrorCode.INVALID_ARGUMENT);

			verify(mockMessaging, times(1)).send(any(Message.class));
		}
	}

	@Test
	@DisplayName("Firebase 전송 실패 시 예외를 던진다 - UNREGISTERED")
	void sendMessageThrowsUnregisteredException() throws FirebaseMessagingException {
		// given
		String token = "unregistered-token";
		String title = "미등록 토큰 테스트";
		String content = "토큰 미등록";
		Map<String, String> data = Map.of();

		FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
		FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
		when(exception.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);
		when(mockMessaging.send(any(Message.class))).thenThrow(exception);

		// when & then
		try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
			mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);

			assertThatThrownBy(() -> fcmSender.send(token, title, content, data))
				.isInstanceOf(FirebaseMessagingException.class)
				.hasFieldOrPropertyWithValue("messagingErrorCode", MessagingErrorCode.UNREGISTERED);

			verify(mockMessaging, times(1)).send(any(Message.class));
		}
	}

	@Test
	@DisplayName("Firebase 전송 실패 시 예외를 던진다 - INTERNAL")
	void sendMessageThrowsInternalException() throws FirebaseMessagingException {
		// given
		String token = "test-token";
		String title = "내부 오류 테스트";
		String content = "서버 내부 오류";
		Map<String, String> data = Map.of("key", "value");

		FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
		FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
		when(exception.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INTERNAL);
		when(mockMessaging.send(any(Message.class))).thenThrow(exception);

		// when & then
		try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
			mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);

			assertThatThrownBy(() -> fcmSender.send(token, title, content, data))
				.isInstanceOf(FirebaseMessagingException.class)
				.hasFieldOrPropertyWithValue("messagingErrorCode", MessagingErrorCode.INTERNAL);

			verify(mockMessaging, times(1)).send(any(Message.class));
		}
	}
}
