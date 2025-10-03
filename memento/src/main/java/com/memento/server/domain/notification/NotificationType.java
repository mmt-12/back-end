package com.memento.server.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

	MEMORY("참여한 새로운 기억 생성"),
	REACTION("내 포스트에 리액션"),
	POST("기억에 새 포스트 등록"),
	ACHIEVE("업적 달성"),
	GUESTBOOK("익명의 방명록 작성"),
	MBTI("새로운 mbti 테스트"),
	NEWIMAGE("새로운 프로필 이미지 등록"),
	BIRTHDAY("오늘의 생일자"),
	ASSOCIATE("새로운 참가자 등장");

	private final String title;
}
