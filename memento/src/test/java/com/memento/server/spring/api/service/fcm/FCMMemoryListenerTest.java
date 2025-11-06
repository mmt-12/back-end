package com.memento.server.spring.api.service.fcm;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_EXISTENCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.memento.server.api.service.fcm.FCMEventHandler;
import com.memento.server.api.service.fcm.FCMEventListener;
import com.memento.server.api.service.fcm.dto.event.AchievementFCM;
import com.memento.server.api.service.fcm.dto.event.AssociateFCM;
import com.memento.server.api.service.fcm.dto.event.BirthdayFCM;
import com.memento.server.api.service.fcm.dto.event.GuestBookFCM;
import com.memento.server.api.service.fcm.dto.event.MbtiFCM;
import com.memento.server.api.service.fcm.dto.event.MemoryFCM;
import com.memento.server.api.service.fcm.dto.event.NewImageFCM;
import com.memento.server.api.service.fcm.dto.event.PostFCM;
import com.memento.server.api.service.fcm.dto.event.ReactionFCM;
import com.memento.server.common.exception.MementoException;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
public class FCMMemoryListenerTest {

	@Autowired
	private FCMEventListener fcmEventListener;

	@MockitoBean
	private FCMEventHandler fcmEventHandler;

	@Test
	@DisplayName("ReactionFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handleReactionNotification() {
		// given
		ReactionFCM event = ReactionFCM.of("actor", 1L, 1L, 1L, 2L);

		// when
		fcmEventListener.handleReactionNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handleReactionNotification(event));
	}

	@Test
	@DisplayName("AchievementFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handleAchievementNotification() {
		// given
		AchievementFCM event = AchievementFCM.of(1L);

		// when
		fcmEventListener.handleAchievementNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handleAchievementNotification(event));
	}

	@Test
	@DisplayName("GuestBookFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handleGuestBookNotification() {
		// given
		GuestBookFCM event = GuestBookFCM.from(1L);

		// when
		fcmEventListener.handleGuestBookNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handleGuestBookNotification(event));
	}

	@Test
	@DisplayName("NewImageFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handleNewImageNotification() {
		// given
		NewImageFCM event = NewImageFCM.from(1L);

		// when
		fcmEventListener.handleNewImageNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handleNewImageNotification(event));
	}

	@Test
	@DisplayName("MbtiFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handleMbtiNotification() {
		// given
		MbtiFCM event = MbtiFCM.from(1L);

		// when
		fcmEventListener.handleMbtiNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handleMbtiNotification(event));
	}

	@Test
	@DisplayName("BirthdayFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handleBirthdayNotification() {
		// given
		BirthdayFCM event = BirthdayFCM.from(1L, 1L);

		// when
		fcmEventListener.handleBirthdayNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handleBirthdayNotification(event));
	}

	@Test
	@DisplayName("MemoryFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handleMemoryNotification() {
		// given
		MemoryFCM event = MemoryFCM.from(1L, 1L);

		// when
		fcmEventListener.handleMemoryNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handleMemoryNotification(event));
	}

	@Test
	@DisplayName("PostFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handlePostNotification() {
		// given
		PostFCM event = PostFCM.of(1L, 1L, 1L);

		// when
		fcmEventListener.handlePostNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handlePostNotification(event));
	}

	@Test
	@DisplayName("AssociateFCM 이벤트를 FCMEventHandler에게 위임한다")
	void handleAssociateNotification() {
		// given
		AssociateFCM event = AssociateFCM.from("newMember", 1L, 1L);

		// when
		fcmEventListener.handleAssociateNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> verify(fcmEventHandler).handleAssociateNotification(event));
	}

	@Test
	@DisplayName("FCMEventHandler에서 MementoException 발생 시 AsyncUncaughtExceptionHandler가 WARN 로그를 남긴다")
	void handleExceptionFromEventHandler_MementoException(CapturedOutput output) {
		// given
		AchievementFCM event = AchievementFCM.of(999L);
		doThrow(new MementoException(ASSOCIATE_NOT_EXISTENCE))
			.when(fcmEventHandler).handleAchievementNotification(event);

		// when
		fcmEventListener.handleAchievementNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> {
				String log = output.toString();
				assertThat(log).contains("FCM Async 비즈니스 예외 발생");
				assertThat(log).contains("ASSOCIATE_NOT_EXISTENCE");
			});
	}

	@Test
	@DisplayName("FCMEventHandler에서 RuntimeException 발생 시 AsyncUncaughtExceptionHandler가 ERROR 로그를 남긴다")
	void handleExceptionFromEventHandler_RuntimeException(CapturedOutput output) {
		// given
		ReactionFCM event = ReactionFCM.of("actor", 1L, 1L, 1L, 2L);
		doThrow(new RuntimeException("Unexpected error"))
			.when(fcmEventHandler).handleReactionNotification(event);

		// when
		fcmEventListener.handleReactionNotification(event);

		// then
		Awaitility.await()
			.atMost(2, TimeUnit.SECONDS)
			.untilAsserted(() -> {
				String log = output.toString();
				assertThat(log).contains("FCM Async 시스템 예외 발생");
				assertThat(log).contains("Unexpected error");
			});
	}
}
