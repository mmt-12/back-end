package com.memento.server.api.service.fcm;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.fcm.dto.AchievementMessageDto;
import com.memento.server.api.service.fcm.dto.BirthdayMessageDto;
import com.memento.server.api.service.fcm.dto.FCMMessageDto;
import com.memento.server.api.service.fcm.dto.GuestBookMessageDto;
import com.memento.server.api.service.fcm.dto.MbtiMessageDto;
import com.memento.server.api.service.fcm.dto.MemoryMessageDto;
import com.memento.server.api.service.fcm.dto.ProfileImageMessageDto;
import com.memento.server.api.service.fcm.dto.ReactionMessageDto;
import com.memento.server.domain.fcm.FCMToken;
import com.memento.server.domain.fcm.FCMTokenRepository;
import com.memento.server.domain.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(propagation = REQUIRES_NEW)
public class FCMService {

	private final NotificationRepository notificationRepository;
	private final FCMTokenRepository fcmTokenRepository;

	public void sendToAssociate(FCMMessageDto fcmMessageDto) {

	}

	public void sendToAssociates(FCMMessageDto fcmMessageDto) {

	}

	// REACTION: 내 포스트에 리액션
	public void sendReactionNotification(ReactionMessageDto request) {

	}

	// ACHIEVE: 업적 달성
	public void sendAchievementNotification(AchievementMessageDto request) {

	}

	// GUESTBOOK: 익명의 방명록 작성
	public void sendGuestBookNotification(GuestBookMessageDto request) {

	}

	// NEWIMAGE: 새로운 프로필 이미지 등록
	public void sendProfileImageNotification(ProfileImageMessageDto request) {

	}

	// MBTI: 새로운 mbti 테스트
	public void sendMbtiNotification(MbtiMessageDto request) {

	}

	// BIRTHDAY: 오늘의 생일자
	public void sendBirthdayNotification(BirthdayMessageDto request) {

	}

	// MEMORY: 참여한 새로운 기억 생성
	public void sendMemoryCreatedNotification(MemoryMessageDto request) {

	}

	// POST: 기억에 새 포스트 등록
	public void sendNewPostNotification(NewPostMessageDto request) {

	}

	// ASSOCIATE: 새로운 참가자 등장
	public void sendNewMemberNotification(NewMemberMessageDto request) {

	}
}
