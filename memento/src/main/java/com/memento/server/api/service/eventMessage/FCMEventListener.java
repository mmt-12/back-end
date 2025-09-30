package com.memento.server.api.service.eventMessage;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_EXISTENCE;
import static com.memento.server.common.error.ErrorCodes.MEMORY_NOT_FOUND;
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

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.memento.server.api.service.eventMessage.dto.AchievementFCM;
import com.memento.server.api.service.eventMessage.dto.AssociateFCM;
import com.memento.server.api.service.eventMessage.dto.BirthdayFCM;
import com.memento.server.api.service.eventMessage.dto.GuestBookFCM;
import com.memento.server.api.service.eventMessage.dto.MbtiFCM;
import com.memento.server.api.service.eventMessage.dto.MemoryFCM;
import com.memento.server.api.service.eventMessage.dto.NewImageFCM;
import com.memento.server.api.service.eventMessage.dto.PostFCM;
import com.memento.server.api.service.eventMessage.dto.ReactionFCM;
import com.memento.server.api.service.fcm.FCMService;
import com.memento.server.api.service.fcm.dto.FCMRequest;
import com.memento.server.api.service.fcm.dto.ReactionData;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Async("appExecutor")
@Service
@Transactional(propagation = REQUIRES_NEW)
@RequiredArgsConstructor
public class FCMEventListener {

	private final FCMService fcmService;
	private final NotificationRepository notificationRepository;
	private final AssociateRepository associateRepository;
	private final MemoryRepository memoryRepository;
	private final MemoryAssociateRepository memoryAssociateRepository;

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleReactionNotification(ReactionFCM event) {
		Associate receiver = associateRepository.getReferenceById(event.receiverId());

		String title = REACTION.getTitle();
		String content = createReactionMessageContent(event.actorNickname());

		Notification notification = Notification.builder()
			.title(title)
			.content(content)
			.type(REACTION)
			.actorId(event.actorId())
			.postId(event.postId())
			.memoryId(event.memoryId())
			.receiver(receiver)
			.build();

		notificationRepository.save(notification);

		ReactionData reactionData = ReactionData.of(event.memoryId(), event.postId());
		FCMRequest request = FCMRequest.of(title, content, List.of(event.receiverId()), reactionData);
		fcmService.sendToAssociates(request);
	}

	// todo 연결 필요
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleAchievementNotification(AchievementFCM event) {
		Associate receiver = associateRepository.findById(event.receiverId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));

		Notification notification = Notification.builder()
			.title(ACHIEVE.getTitle())
			.content(createAchievementMessageContent(receiver.getNickname()))
			.type(ACHIEVE)
			.actorId(receiver.getId())
			.receiver(receiver)
			.build();

		notificationRepository.save(notification);

		fcmService.sendToAssociates(FCMRequest.of());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleGuestBookNotification(GuestBookFCM event) {
		Associate receiver = associateRepository.getReferenceById(event.receiverId());

		Notification notification = Notification.builder()
			.title(GUESTBOOK.getTitle())
			.content(createGuestBookMessageContent())
			.type(GUESTBOOK)
			.receiver(receiver)
			.build();

		notificationRepository.save(notification);

		fcmService.sendToAssociates(FCMRequest.of());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleNewImageNotification(NewImageFCM event) {
		Associate associate = associateRepository.getReferenceById(event.receiverId());

		Notification notification = Notification.builder()
			.title(NEWIMAGE.getTitle())
			.content(createProfileImageMessageContent())
			.type(NEWIMAGE)
			.receiver(associate)
			.build();

		notificationRepository.save(notification);

		fcmService.sendToAssociates(FCMRequest.of());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMbtiNotification(MbtiFCM event) {
		Associate associate = associateRepository.getReferenceById(event.receiverId());

		Notification notification = Notification.builder()
			.title(MBTI.getTitle())
			.content(createMbtiMessageContent())
			.type(MBTI)
			.receiver(associate)
			.build();

		notificationRepository.save(notification);

		fcmService.sendToAssociates(FCMRequest.of());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleBirthdayNotification(BirthdayFCM event) {
		Associate birthdayPerson = associateRepository.findById(event.birthdayAssociateId())
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

		Notification notification = Notification.builder()
			.title(BIRTHDAY.getTitle())
			.content(createBirthdayMessageContentForBirthdayPerson(birthdayPerson.getNickname()))
			.type(BIRTHDAY)
			.actorId(birthdayPerson.getId())
			.receiver(birthdayPerson)
			.build();

		notificationRepository.saveAll(notifications);
		notificationRepository.save(notification);

		fcmService.sendToAssociates(FCMRequest.of());
		fcmService.sendToAssociates(FCMRequest.of());
	}

	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleMemoryNotification(MemoryFCM event) {
		List<Associate> associates = memoryAssociateRepository.findAssociatesByMemoryIdAndDeletedAtIsNull(
			event.memoryId());

		List<Notification> notificationList = associates.stream()
			.filter(associate -> !associate.getId().equals(event.makeMemoryAssociateId()))
			.map(associate -> Notification.builder()
				.title(MEMORY.getTitle())
				.content(createMemoryMessageContent(associate.getNickname()))
				.type(MEMORY)
				.actorId(event.makeMemoryAssociateId())
				.memoryId(event.memoryId())
				.receiver(associate)
				.build())
			.toList();

		notificationRepository.saveAll(notificationList);

		fcmService.sendToAssociates(FCMRequest.of());
	}

	// todo POST: 기억에 새 포스트 등록
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handlePostNotification(PostFCM event) {
		Memory memory = memoryRepository.findByIdWithEventAndDeletedAtIsNull(event.memoryId())
			.orElseThrow(() -> new MementoException(MEMORY_NOT_FOUND));
		List<Associate> associates = memoryAssociateRepository.findAssociatesByMemoryIdAndDeletedAtIsNull(
			event.memoryId());

		List<Notification> notificationList = associates.stream()
			.filter(associate -> !associate.getId().equals(event.makePostAssociateId()))
			.map(associate -> Notification.builder()
				.title(POST.getTitle())
				.content(createPostMessageContent(memory.getEvent().getTitle()))
				.type(POST)
				.actorId(event.makePostAssociateId())
				.postId(event.postId())
				.memoryId(event.memoryId())
				.receiver(associate)
				.build())
			.toList();

		notificationRepository.saveAll(notificationList);

		fcmService.sendToAssociates(FCMRequest.of());
	}

	// todo ASSOCIATE: 새로운 참가자 등장
	@TransactionalEventListener(phase = AFTER_COMMIT)
	public void handleAssociateNotification(AssociateFCM event) {
		List<Associate> associates = associateRepository.findAllByCommunityId(event.communityId());

		List<Notification> notifications = associates.stream()
			.filter(associate -> !associate.getId().equals(event.associateId()))
			.map(associate -> Notification.builder()
				.title(ASSOCIATE.getTitle())
				.content(createAssociateMessageContent(event.nickname()))
				.type(ASSOCIATE)
				.actorId(event.associateId())
				.receiver(associate)
				.build())
			.toList();

		notificationRepository.saveAll(notifications);

		fcmService.sendToAssociates(FCMRequest.of());
	}

	private String createReactionMessageContent(String nickname) {
		return String.format("%s님이 포스트에 반응을 남겼어요.", nickname);
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

	private String createPostMessageContent(String eventTitle) {
		return String.format("%s에 새로운 포스트가 올라왔어요. 확인해보세요.", eventTitle);
	}

	private String createAssociateMessageContent(String nickname) {
		return String.format("%s님이 가입했어요. 방명록에 환영의 메세지를 남겨주세요!", nickname);
	}
}
