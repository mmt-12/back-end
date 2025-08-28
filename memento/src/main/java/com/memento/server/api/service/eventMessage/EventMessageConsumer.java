package com.memento.server.api.service.eventMessage;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_EXISTENCE;
import static com.memento.server.domain.notification.NotificationType.ASSOCIATE;
import static com.memento.server.domain.notification.NotificationType.MBTI;
import static com.memento.server.domain.notification.NotificationType.MEMORY;
import static com.memento.server.domain.notification.NotificationType.NEWIMAGE;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.memento.server.api.service.eventMessage.dto.AssociateNotification;
import com.memento.server.api.service.eventMessage.dto.MbtiNotification;
import com.memento.server.api.service.eventMessage.dto.MemoryNotification;
import com.memento.server.api.service.eventMessage.dto.NewImageNotification;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Async
@Service
@Transactional(propagation = REQUIRES_NEW)
@RequiredArgsConstructor
public class EventMessageConsumer {

	private final NotificationRepository notificationRepository;
	private final AssociateRepository associateRepository;
	private final MemoryAssociateRepository memoryAssociateRepository;

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleNewImageNotification(NewImageNotification event) {
		Associate associate = associateRepository.findById(event.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));

		notificationRepository.save(Notification.builder()
			.title(NEWIMAGE.getTitle())
			.content(NEWIMAGE.getTitle())
			.type(NEWIMAGE)
			.actorId(event.associateId())
			.receiver(associate)
			.build());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMbtiNotification(MbtiNotification event) {
		Associate associate = associateRepository.findById(event.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));

		notificationRepository.save(Notification.builder()
			.title(MBTI.getTitle())
			.content(MBTI.getTitle())
			.type(MBTI)
			.actorId(event.associateId())
			.receiver(associate)
			.build());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleAssociateNotification(AssociateNotification event) {
		List<Associate> associates = associateRepository.findAllByCommunityId(event.communityId());

		List<Notification> notificationList = new ArrayList<>();
		for (Associate associate : associates) {
			if (associate.getId().equals(event.associateId()))
				continue;
			notificationList.add(Notification.builder()
				.title(ASSOCIATE.getTitle())
				.content(ASSOCIATE.getTitle())
				.type(ASSOCIATE)
				.actorId(event.associateId())
				.receiver(associate)
				.build());
		}

		notificationRepository.saveAll(notificationList);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMemoryNotification(MemoryNotification event) {
		List<Associate> associates = memoryAssociateRepository.findAllAssociatesByMemoryId(event.memoryId());

		List<Notification> notificationList = new ArrayList<>();
		for (Associate associate : associates) {
			if (associate.getId().equals(event.authorId()))
				continue;
			notificationList.add(Notification.builder()
				.title(MEMORY.getTitle())
				.content(MEMORY.getTitle())
				.type(MEMORY)
				.receiver(associate)
				.memoryId(event.memoryId())
				.build());
		}

		notificationRepository.saveAll(notificationList);
	}
}
