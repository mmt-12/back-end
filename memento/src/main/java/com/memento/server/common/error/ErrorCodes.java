package com.memento.server.common.error;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes implements ErrorCode {

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1000, "내부 서버 오류"),
	INVALID_INPUT_VALUE(BAD_REQUEST, 1001, "잘못된 입력"),
	MISSING_REQUEST_PART(BAD_REQUEST, 1002, "필수 요청 part 누락"),
	MULTIPART_TOO_LARGE(PAYLOAD_TOO_LARGE, 1003, "요청 파일 크기가 허용 범위를 초과"),

	VOICE_NAME_REQUIRED(BAD_REQUEST, 2000, "보이스 이름은 필수입니다."),
	VOICE_NAME_BLANK(BAD_REQUEST, 2001, "보이스 이름은 공백일 수 없습니다."),
	VOICE_NAME_TOO_LONG(BAD_REQUEST, 2002, "보이스 이름은 최대 102자까지 입력할 수 있습니다."),
	VOICE_URL_REQUIRED(BAD_REQUEST, 2003, "보이스 URL은 필수입니다."),
	VOICE_URL_BLANK(BAD_REQUEST, 2004, "보이스 URL은 공백일 수 없습니다."),
	VOICE_URL_TOO_LONG(BAD_REQUEST, 2005, "보이스 URL은 최대 255자까지 입력할 수 있습니다."),
	VOICE_ASSOCIATE_REQUIRED(BAD_REQUEST, 2006, "보이스 작성자는 필수입니다."),
	VOICE_SAVE_FAIL(BAD_REQUEST, 2007, "보이스 저장을 실패하였습니다."),

	EMOJI_NAME_REQUIRED(BAD_REQUEST, 3000, "이모지 이름은 필수입니다."),
	EMOJI_NAME_BLANK(BAD_REQUEST, 3001, "이모지 이름은 공백일 수 없습니다."),
	EMOJI_NAME_TOO_LONG(BAD_REQUEST, 3002, "이모지 이름은 최대 102자까지 입력할 수 있습니다."),
	EMOJI_URL_REQUIRED(BAD_REQUEST, 3003, "이모지 URL은 필수입니다."),
	EMOJI_URL_BLANK(BAD_REQUEST, 3004, "이모지 URL은 공백일 수 없습니다."),
	EMOJI_URL_TOO_LONG(BAD_REQUEST, 3005, "이모지 URL은 최대 255자까지 입력할 수 있습니다."),
	EMOJI_ASSOCIATE_REQUIRED(BAD_REQUEST, 3006, "이모지 작성자는 필수입니다."),

	MEMBER_NAME_REQUIRED(BAD_REQUEST, 4000, "회원 이름은 필수입니다."),
	MEMBER_NAME_BLANK(BAD_REQUEST, 4001, "회원 이름은 공백일 수 없습니다."),
	MEMBER_NAME_TOO_LONG(BAD_REQUEST, 4002, "회원 이름은 102자 이하로 입력해야 합니다."),
	MEMBER_EMAIL_REQUIRED(BAD_REQUEST, 4003, "이메일은 필수입니다."),
	MEMBER_EMAIL_BLANK(BAD_REQUEST, 4004, "이메일은 공백일 수 없습니다."),
	MEMBER_EMAIL_TOO_LONG(BAD_REQUEST, 4005, "이메일은 255자 이하로 입력해야 합니다."),
	MEMBER_EMAIL_INVALID_FORMAT(BAD_REQUEST, 4006, "이메일 형식이 올바르지 않습니다."),
	MEMBER_BIRTHDAY_IN_FUTURE(BAD_REQUEST, 4007, "생년월일은 미래일 수 없습니다."),
	MEMBER_KAKAO_ID_REQUIRED(BAD_REQUEST, 4008, "카카오 ID는 필수입니다."),

	COMMUNITY_NAME_REQUIRED(BAD_REQUEST, 5000, "커뮤니티 이름은 필수입니다."),
	COMMUNITY_NAME_BLANK(BAD_REQUEST, 5001, "커뮤니티 이름은 공백일 수 없습니다."),
	COMMUNITY_NAME_TOO_LONG(BAD_REQUEST, 5002, "커뮤니티 이름은 102자 이하로 입력해야 합니다."),
	COMMUNITY_MEMBER_REQUIRED(BAD_REQUEST, 5003, "커뮤니티 생성자는 필수입니다."),
	COMMUNITY_NOT_MATCH(BAD_REQUEST, 7005, "다른 그룹의 요청입니다."),

	ACHIEVEMENT_NAME_REQUIRED(BAD_REQUEST, 6000, "업적 이름은 필수입니다."),
	ACHIEVEMENT_NAME_BLANK(BAD_REQUEST, 6001, "업적 이름은 공백일 수 없습니다."),
	ACHIEVEMENT_NAME_TOO_LONG(BAD_REQUEST, 6002, "업적 이름은 102자 이하로 입력해야 합니다."),
	ACHIEVEMENT_CRITERIA_REQUIRED(BAD_REQUEST, 6003, "업적 기준은 필수입니다."),
	ACHIEVEMENT_CRITERIA_BLANK(BAD_REQUEST, 6004, "업적 기준은 공백일 수 없습니다."),
	ACHIEVEMENT_CRITERIA_TOO_LONG(BAD_REQUEST, 6005, "업적 기준은 255자 이하로 입력해야 합니다."),
	ACHIEVEMENT_TYPE_REQUIRED(BAD_REQUEST, 6006, "업적 타입은 필수입니다."),
	ACHIEVEMENT_NOT_EXISTENCE(BAD_REQUEST, 6007, "존재하지 않는 업적입니다."),


	ASSOCIATE_NICKNAME_REQUIRED(BAD_REQUEST, 7000, "그룹 참여자 닉네임은 필수입니다."),
	ASSOCIATE_NICKNAME_BLANK(BAD_REQUEST, 7001, "그룹 참여자 닉네임은 공백일 수 없습니다."),
	ASSOCIATE_NICKNAME_TOO_LONG(BAD_REQUEST, 7002, "그룹 참여자 닉네임은 51자 이하로 입력해야 합니다."),
	ASSOCIATE_MEMBER_REQUIRED(BAD_REQUEST, 7003, "그룹 참여자 회원은 필수입니다."),
	ASSOCIATE_COMMUNITY_REQUIRED(BAD_REQUEST, 7004, "그룹 참여자 커뮤니티는 필수입니다."),
	ASSOCIATE_INVALID(BAD_REQUEST, 7005, "그룹 참여자 커뮤니티는 필수입니다."),
	ASSOCIATE_NOT_AUTHORITY(BAD_REQUEST, 7006, "권한이 없는 참여자입니다."),
	ASSOCIATE_NOT_EXISTENCE(BAD_REQUEST, 7007, "존재하지 않는 참여자 입니다."),
	ASSOCIATE_COMMUNITY_NOT_MATCH(BAD_REQUEST, 7008, "해당 커뮤니티의 참가자가 아닙니다."),

	GUESTBOOK_NOT_EXISTENCE(BAD_REQUEST, 8000, "존재하지 않는 방명록입니다."),

	PROFILEIMAGE_NOT_EXISTENCE(BAD_REQUEST, 9000, "존재하지 않는 프로필 이미지입니다."),
	PROFILEIMAGE_SAVE_FAIL(BAD_REQUEST, 9001, "프로필 이미지 저장에 실패하였습니다."),

	MBTI_NOT_EXISTENCE(BAD_REQUEST, 10000, "존재 하지 않는 MBTI입니다."),

	COMMENT_URL_REQUIRED(BAD_REQUEST, 8000, "코멘트 URL은 필수입니다."),
	COMMENT_URL_BLANK(BAD_REQUEST, 8001, "코멘트 URL은 공백일 수 없습니다."),
	COMMENT_URL_TOO_LONG(BAD_REQUEST, 8002, "코멘트 URL은 최대 255자까지 입력할 수 있습니다."),
	COMMENT_POST_REQUIRED(BAD_REQUEST, 8003, "코멘트 게시글은 필수입니다."),
	COMMENT_ASSOCIATE_REQUIRED(BAD_REQUEST, 8004, "코멘트 작성자는 필수입니다."),
	;
	private final HttpStatus status;
	private final int code;
	private final String message;
}
