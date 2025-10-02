package com.memento.server.api.service.fcm;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.memento.server.api.service.fcm.dto.event.AchievementFCM;
import com.memento.server.api.service.fcm.dto.event.AssociateFCM;
import com.memento.server.api.service.fcm.dto.event.BirthdayFCM;
import com.memento.server.api.service.fcm.dto.event.GuestBookFCM;
import com.memento.server.api.service.fcm.dto.event.MbtiFCM;
import com.memento.server.api.service.fcm.dto.event.MemoryFCM;
import com.memento.server.api.service.fcm.dto.event.NewImageFCM;
import com.memento.server.api.service.fcm.dto.event.PostFCM;
import com.memento.server.api.service.fcm.dto.event.ReactionFCM;

import lombok.RequiredArgsConstructor;

@Async("appExecutor")
@Component
@RequiredArgsConstructor
public class FCMEventListener {

	private final FCMEventHandler eventHandler;

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleReactionNotification(ReactionFCM event) {
		eventHandler.handleReactionNotification(event);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleAchievementNotification(AchievementFCM event) {
		eventHandler.handleAchievementNotification(event);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleGuestBookNotification(GuestBookFCM event) {
		eventHandler.handleGuestBookNotification(event);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleNewImageNotification(NewImageFCM event) {
		eventHandler.handleNewImageNotification(event);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMbtiNotification(MbtiFCM event) {
		eventHandler.handleMbtiNotification(event);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleBirthdayNotification(BirthdayFCM event) {
		eventHandler.handleBirthdayNotification(event);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMemoryNotification(MemoryFCM event) {
		eventHandler.handleMemoryNotification(event);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handlePostNotification(PostFCM event) {
		eventHandler.handlePostNotification(event);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleAssociateNotification(AssociateFCM event) {
		eventHandler.handleAssociateNotification(event);
	}
}
