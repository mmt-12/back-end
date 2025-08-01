package com.memento.server.common.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.PAYLOAD_TOO_LARGE;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes implements ErrorCode {

	SERVER_ERROR(INTERNAL_SERVER_ERROR, 1000, "내부 서버 오류"),
	INVALID_INPUT_VALUE(BAD_REQUEST, 1001, "잘못된 입력"),
	MISSING_REQUEST_PART(BAD_REQUEST, 1002, "필수 요청 part 누락"),
	MULTIPART_TOO_LARGE(PAYLOAD_TOO_LARGE, 1003, "요청 파일 크기가 허용 범위를 초과"),

	;
	private final HttpStatus status;
	private final int code;
	private final String message;
}
