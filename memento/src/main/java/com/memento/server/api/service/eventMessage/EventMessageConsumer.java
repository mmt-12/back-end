package com.memento.server.api.service.eventMessage;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_EXISTENCE;
import static com.memento.server.domain.notification.NotificationType.ACHIEVE;
import static com.memento.server.domain.notification.NotificationType.ASSOCIATE;
import static com.memento.server.domain.notification.NotificationType.BIRTHDAY;
import static com.memento.server.domain.notification.NotificationType.GUESTBOOK;
import static com.memento.server.domain.notification.NotificationType.MBTI;
import static com.memento.server.domain.notification.NotificationType.MEMORY;
import static com.memento.server.domain.notification.NotificationType.NEWIMAGE;
import static com.memento.server.domain.notification.NotificationType.POST;
import static com.memento.server.domain.notification.NotificationType.REACTION;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.memento.server.api.service.eventMessage.dto.AchievementNotification;
import com.memento.server.api.service.eventMessage.dto.AssociateNotification;
import com.memento.server.api.service.eventMessage.dto.BirthdayNotification;
import com.memento.server.api.service.eventMessage.dto.GuestBookNotification;
import com.memento.server.api.service.eventMessage.dto.MbtiNotification;
import com.memento.server.api.service.eventMessage.dto.MemoryNotification;
import com.memento.server.api.service.eventMessage.dto.NewImageNotification;
import com.memento.server.api.service.eventMessage.dto.PostNotification;
import com.memento.server.api.service.eventMessage.dto.ReactionNotification;
import com.memento.server.api.service.fcm.FCMService;
import com.memento.server.api.service.fcm.dto.AchievementMessageDto;
import com.memento.server.api.service.fcm.dto.BirthdayMessageDto;
import com.memento.server.api.service.fcm.dto.GuestBookMessageDto;
import com.memento.server.api.service.fcm.dto.MbtiMessageDto;
import com.memento.server.api.service.fcm.dto.MemoryMessageDto;
import com.memento.server.api.service.fcm.dto.ProfileImageMessageDto;
import com.memento.server.api.service.fcm.dto.ReactionMessageDto;
import com.memento.server.api.service.fcm.dto.ReceiverInfo;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Async("appExecutor")
@Service
@Transactional(propagation = REQUIRES_NEW)
@RequiredArgsConstructor
public class EventMessageConsumer {

	private final FCMService fcmService;
	private final NotificationRepository notificationRepository;
	private final AssociateRepository associateRepository;

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleReactionNotification(ReactionNotification event) {
		Associate associate = associateRepository.getReferenceById(event.receiverId());
		Notification notification = Notification.builder()
			.title(event.title())
			.content(event.content())
			.type(REACTION)
			.actorId(event.actorId())
			.postId(event.postId())
			.memoryId(event.memoryId())
			.receiver(associate)
			.build();

		notificationRepository.save(notification);

		fcmService.sendReactionNotification(ReactionMessageDto.from(notification));
	}

	// todo 연결 필요
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleAchievementNotification(AchievementNotification event) {
		Associate associate = associateRepository.getReferenceById(event.receiverId());

		Notification notification = Notification.builder()
			.title(ACHIEVE.getTitle())
			.content(createAchievementMessageContent(associate.getNickname()))
			.type(ACHIEVE)
			.receiver(associate)
			.build();

		notificationRepository.save(notification);

		fcmService.sendAchievementNotification(AchievementMessageDto.from(notification));
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleGuestBookNotification(GuestBookNotification event) {
		Associate associate = associateRepository.getReferenceById(event.receiverId());

		Notification notification = Notification.builder()
			.title(GUESTBOOK.getTitle())
			.content(createGuestBookMessageContent())
			.type(GUESTBOOK)
			.receiver(associate)
			.build();

		notificationRepository.save(notification);

		fcmService.sendGuestBookNotification(GuestBookMessageDto.from(notification));
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleNewImageNotification(NewImageNotification event) {
		Associate associate = associateRepository.getReferenceById(event.receiverId());

		Notification notification = Notification.builder()
			.title(NEWIMAGE.getTitle())
			.content(createProfileImageMessageContent())
			.type(NEWIMAGE)
			.receiver(associate)
			.build();

		notificationRepository.save(notification);

		fcmService.sendProfileImageNotification(ProfileImageMessageDto.from(notification));
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMbtiNotification(MbtiNotification event) {
		Associate associate = associateRepository.getReferenceById(event.receiverId());

		Notification notification = Notification.builder()
			.title(MBTI.getTitle())
			.content(createMbtiMessageContent())
			.type(MBTI)
			.receiver(associate)
			.build();

		notificationRepository.save(notification);

		fcmService.sendMbtiNotification(MbtiMessageDto.from(notification));
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleBirthdayNotification(BirthdayNotification event) {
		Associate birthdayPerson = associateRepository.findById(event.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));
		List<Associate> associates = associateRepository.findAllByCommunityId(event.communityId());

		List<Notification> notifications = associates.stream()
			.filter(associate -> !associate.equals(birthdayPerson))
			.map(associate -> Notification.builder()
				.title(BIRTHDAY.getTitle())
				.content(createBirthdayMessageContentForAssociates(birthdayPerson.getNickname()))
				.type(BIRTHDAY)
				.actorId(birthdayPerson.getId())
				.receiver(associate)
				.build())
			.toList();

		notifications.add(Notification.builder()
			.title(BIRTHDAY.getTitle())
			.content(createBirthdayMessageContentForBirthdayPerson(birthdayPerson.getNickname()))
			.type(BIRTHDAY)
			.actorId(birthdayPerson.getId())
			.receiver(birthdayPerson)
			.build());

		notificationRepository.saveAll(notifications);

		List<ReceiverInfo> receivers = notifications.stream()
			.map(notification -> ReceiverInfo.builder()
				.receiverId(notification.getReceiver().getId())
				.content(notification.getContent())
				.build())
			.toList();

		BirthdayMessageDto birthdayMessageDto = BirthdayMessageDto.from(BIRTHDAY.getTitle(), BIRTHDAY,
			birthdayPerson.getId(), receivers);

		fcmService.sendBirthdayNotification(birthdayMessageDto);
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMemoryNotification(MemoryNotification event) {
		List<Associate> associates = associateRepository.findAllByMemoryId(event.memoryId());

		List<Notification> notificationList = associates.stream()
			.filter(associate -> !associate.getId().equals(event.authorId()))
			.map(associate -> Notification.builder()
				.title(MEMORY.getTitle())
				.content(createMemoryMessageContent(associate.getNickname()))
				.type(MEMORY)
				.receiver(associate)
				.memoryId(event.memoryId())
				.build())
			.toList();

		notificationRepository.saveAll(notificationList);

		List<ReceiverInfo> receivers = notificationList.stream()
			.map(notification -> ReceiverInfo.builder()
				.receiverId(notification.getReceiver().getId())
				.content(notification.getContent())
				.build())
			.toList();

		MemoryMessageDto memoryMessageDto = MemoryMessageDto.from(MEMORY.getTitle(), MEMORY, event.memoryId(),
			receivers);

		fcmService.sendMemoryCreatedNotification(memoryMessageDto);
	}

	// todo POST: 기억에 새 포스트 등록
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handlePostNotification(PostNotification event) {
		List<Associate> associates = associateRepository.findAllByMemoryId(event.memoryId());

		List<Notification> notificationList = new ArrayList<>();
		for (Associate associate : associates) {
			if (associate.getId().equals(event.actorId()))
				continue;
			notificationList.add(Notification.builder()
				.title(POST.getTitle())
				.content(POST.getTitle())
				.type(POST)
				.receiver(associate)
				.postId(event.postId())
				.memoryId(event.memoryId())
				.build());
		}

		notificationRepository.saveAll(notificationList);
	}

	// todo ASSOCIATE: 새로운 참가자 등장
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
				.receiver(associate)
				.build());
		}

		notificationRepository.saveAll(notificationList);
	}

	private String createMemoryMessageContent(String nickname) {
		return String.format("%s님이 참가한 새로운 기억이 추가되었어요.", nickname);
	}

	private String createGuestBookMessageContent() {
		return "누군가가 내 방명록에 글을 작성했어요.";
	}

	private String createProfileImageMessageContent() {
		return "새로운 프로필 이미지가 등록되었습니다.";
	}

	private String createMbtiMessageContent() {
		return "새로운 MBTI 평가가 추가되었습니다. 결과를 확인 해보세요.";
	}

	private String createAchievementMessageContent(String nickname) {
		return String.format("%s님, 새로운 업적을 달성했어요! 획득한 칭호를 확인해보세요.", nickname);
	}

	private String createBirthdayMessageContentForAssociates(String nickname) {
		return String.format("오늘은 %s님의 생일입니다!! 방명록에 축하의 메세지를 남겨주세요.", nickname);
	}

	private String createBirthdayMessageContentForBirthdayPerson(String nickname) {
		return String.format("%s님 생일 축하드립니다!! 방명록에서 축하의 메세지를 확인해보세요.", nickname);
	}
}
