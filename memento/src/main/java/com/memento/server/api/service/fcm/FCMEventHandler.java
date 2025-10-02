package com.memento.server.api.service.fcm;

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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.fcm.dto.event.AchievementFCM;
import com.memento.server.api.service.fcm.dto.event.AssociateFCM;
import com.memento.server.api.service.fcm.dto.event.BirthdayFCM;
import com.memento.server.api.service.fcm.dto.event.GuestBookFCM;
import com.memento.server.api.service.fcm.dto.event.MbtiFCM;
import com.memento.server.api.service.fcm.dto.event.MemoryFCM;
import com.memento.server.api.service.fcm.dto.event.NewImageFCM;
import com.memento.server.api.service.fcm.dto.event.PostFCM;
import com.memento.server.api.service.fcm.dto.event.ReactionFCM;
import com.memento.server.api.service.fcm.dto.request.AssociateData;
import com.memento.server.api.service.fcm.dto.request.BasicData;
import com.memento.server.api.service.fcm.dto.request.BirthdayData;
import com.memento.server.api.service.fcm.dto.request.FCMRequest;
import com.memento.server.api.service.fcm.dto.request.ReceiverInfo;
import com.memento.server.api.service.fcm.dto.request.MemoryData;
import com.memento.server.api.service.fcm.dto.request.PostData;
import com.memento.server.api.service.fcm.dto.request.ReactionData;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.notification.Notification;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation = REQUIRES_NEW)
@RequiredArgsConstructor
public class FCMEventHandler {

	private final FCMService fcmService;
	private final NotificationRepository notificationRepository;
	private final AssociateRepository associateRepository;
	private final MemoryRepository memoryRepository;
	private final MemoryAssociateRepository memoryAssociateRepository;

	public void handleReactionNotification(ReactionFCM event) {
		Associate receiver = associateRepository.findById(event.receiverId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));

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
		ReceiverInfo receiverInfo = ReceiverInfo.of(event.receiverId(), content);
		FCMRequest request = FCMRequest.of(title, List.of(receiverInfo), reactionData);
		fcmService.sendToAssociates(request);
	}

	public void handleAchievementNotification(AchievementFCM event) {
		Associate receiver = associateRepository.findById(event.receiverId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));

		String title = ACHIEVE.getTitle();
		String content = createAchievementMessageContent(receiver.getNickname());

		Notification notification = Notification.builder()
			.title(title)
			.content(content)
			.type(ACHIEVE)
			.actorId(receiver.getId())
			.receiver(receiver)
			.build();

		notificationRepository.save(notification);

		BasicData achievementData = BasicData.of(ACHIEVE);
		ReceiverInfo receiverInfo = ReceiverInfo.of(event.receiverId(), content);
		FCMRequest request = FCMRequest.of(title, List.of(receiverInfo), achievementData);
		fcmService.sendToAssociates(request);
	}

	public void handleGuestBookNotification(GuestBookFCM event) {
		Associate receiver = associateRepository.findById(event.receiverId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));

		String title = GUESTBOOK.getTitle();
		String content = createGuestBookMessageContent();

		Notification notification = Notification.builder()
			.title(title)
			.content(content)
			.type(GUESTBOOK)
			.receiver(receiver)
			.build();

		notificationRepository.save(notification);

		BasicData guestBookData = BasicData.of(GUESTBOOK);
		ReceiverInfo receiverInfo = ReceiverInfo.of(event.receiverId(), content);
		FCMRequest request = FCMRequest.of(title, List.of(receiverInfo), guestBookData);
		fcmService.sendToAssociates(request);
	}

	public void handleNewImageNotification(NewImageFCM event) {
		Associate receiver = associateRepository.findById(event.receiverId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));

		String title = NEWIMAGE.getTitle();
		String content = createProfileImageMessageContent();

		Notification notification = Notification.builder()
			.title(title)
			.content(content)
			.type(NEWIMAGE)
			.receiver(receiver)
			.build();

		notificationRepository.save(notification);

		BasicData newImageData = BasicData.of(NEWIMAGE);
		ReceiverInfo receiverInfo = ReceiverInfo.of(event.receiverId(), content);
		FCMRequest request = FCMRequest.of(title, List.of(receiverInfo), newImageData);
		fcmService.sendToAssociates(request);
	}

	public void handleMbtiNotification(MbtiFCM event) {
		Associate receiver = associateRepository.findById(event.receiverId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));

		String title = MBTI.getTitle();
		String content = createMbtiMessageContent();

		Notification notification = Notification.builder()
			.title(title)
			.content(content)
			.type(MBTI)
			.receiver(receiver)
			.build();

		notificationRepository.save(notification);

		BasicData mbtiData = BasicData.of(MBTI);
		ReceiverInfo receiverInfo = ReceiverInfo.of(event.receiverId(), content);
		FCMRequest request = FCMRequest.of(title, List.of(receiverInfo), mbtiData);
		fcmService.sendToAssociates(request);
	}

	public void handleBirthdayNotification(BirthdayFCM event) {
		Associate birthdayPerson = associateRepository.findById(event.birthdayAssociateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_EXISTENCE));
		List<Associate> associates = associateRepository.findAllByCommunityId(event.communityId());

		String title = BIRTHDAY.getTitle();
		String contentForAssociates = createBirthdayMessageContentForAssociates(birthdayPerson.getNickname());
		String contentForBirthdayPerson = createBirthdayMessageContentForBirthdayPerson(birthdayPerson.getNickname());

		List<Notification> associateNotifications = associates.stream()
			.filter(associate -> !associate.equals(birthdayPerson))
			.map(associate -> Notification.builder()
				.title(title)
				.content(contentForAssociates)
				.type(BIRTHDAY)
				.actorId(birthdayPerson.getId())
				.receiver(associate)
				.build())
			.toList();

		Notification birthdayPersonNotification = Notification.builder()
			.title(title)
			.content(contentForBirthdayPerson)
			.type(BIRTHDAY)
			.actorId(birthdayPerson.getId())
			.receiver(birthdayPerson)
			.build();

		List<Notification> allNotifications = new ArrayList<>(associateNotifications);
		allNotifications.add(birthdayPersonNotification);

		notificationRepository.saveAll(allNotifications);

		BirthdayData birthdayData = BirthdayData.of(birthdayPerson.getId());
		List<ReceiverInfo> allReceivers = allNotifications.stream()
			.map(n -> ReceiverInfo.of(n.getReceiver().getId(), n.getContent()))
			.toList();
		FCMRequest request = FCMRequest.of(BIRTHDAY.getTitle(), allReceivers, birthdayData);
		fcmService.sendToAssociates(request);
	}

	public void handleMemoryNotification(MemoryFCM event) {
		List<Associate> associates = memoryAssociateRepository.findAssociatesByMemoryIdAndDeletedAtIsNull(
			event.memoryId());

		String title = MEMORY.getTitle();
		List<Notification> notificationList = associates.stream()
			.filter(associate -> !associate.getId().equals(event.makeMemoryAssociateId()))
			.map(associate -> Notification.builder()
				.title(title)
				.content(createMemoryMessageContent(associate.getNickname()))
				.type(MEMORY)
				.actorId(event.makeMemoryAssociateId())
				.memoryId(event.memoryId())
				.receiver(associate)
				.build())
			.toList();

		if (!notificationList.isEmpty()) {
			notificationRepository.saveAll(notificationList);

			List<ReceiverInfo> receiverInfos = notificationList.stream()
				.map(n -> ReceiverInfo.of(n.getReceiver().getId(), n.getContent()))
				.toList();

			MemoryData memoryData = MemoryData.of(event.memoryId());
			FCMRequest request = FCMRequest.of(title, receiverInfos, memoryData);
			fcmService.sendToAssociates(request);
		}
	}

	public void handlePostNotification(PostFCM event) {
		Memory memory = memoryRepository.findByIdWithEventAndDeletedAtIsNull(event.memoryId())
			.orElseThrow(() -> new MementoException(MEMORY_NOT_FOUND));
		List<Associate> associates = memoryAssociateRepository.findAssociatesByMemoryIdAndDeletedAtIsNull(
			event.memoryId());

		String title = POST.getTitle();
		String content = createPostMessageContent(memory.getEvent().getTitle());
		List<Notification> notificationList = associates.stream()
			.filter(associate -> !associate.getId().equals(event.makePostAssociateId()))
			.map(associate -> Notification.builder()
				.title(title)
				.content(content)
				.type(POST)
				.actorId(event.makePostAssociateId())
				.postId(event.postId())
				.memoryId(event.memoryId())
				.receiver(associate)
				.build())
			.toList();

		if (!notificationList.isEmpty()) {
			notificationRepository.saveAll(notificationList);

			List<ReceiverInfo> receiverInfos = notificationList.stream()
				.map(n -> ReceiverInfo.of(n.getReceiver().getId(), n.getContent()))
				.toList();

			PostData postData = PostData.of(event.postId(), event.memoryId());
			FCMRequest request = FCMRequest.of(title, receiverInfos, postData);
			fcmService.sendToAssociates(request);
		}
	}

	public void handleAssociateNotification(AssociateFCM event) {
		List<Associate> associates = associateRepository.findAllByCommunityId(event.communityId());

		String title = ASSOCIATE.getTitle();
		String content = createAssociateMessageContent(event.nickname());

		List<Notification> notifications = associates.stream()
			.filter(associate -> !associate.getId().equals(event.associateId()))
			.map(associate -> Notification.builder()
				.title(title)
				.content(content)
				.type(ASSOCIATE)
				.actorId(event.associateId())
				.receiver(associate)
				.build())
			.toList();

		if(!notifications.isEmpty()) {
			notificationRepository.saveAll(notifications);

			List<ReceiverInfo> receiverInfos = notifications.stream()
				.map(n -> ReceiverInfo.of(n.getReceiver().getId(), content))
				.toList();

			AssociateData associateData = AssociateData.of(event.associateId());
			FCMRequest request = FCMRequest.of(title, receiverInfos, associateData);
			fcmService.sendToAssociates(request);
		}
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
