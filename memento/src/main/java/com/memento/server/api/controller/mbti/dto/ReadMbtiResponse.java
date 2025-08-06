package com.memento.server.api.controller.mbti.dto;

import lombok.Builder;

@Builder
public record ReadMbtiResponse(
	int INFP,
	int INFJ,
	int INTP,
	int INTJ,
	int ISFP,
	int ISFJ,
	int ISTP,
	int ISTJ,
	int ENFP,
	int ENFJ,
	int ENTP,
	int ENTJ,
	int ESFP,
	int ESFJ,
	int ESTP,
	int ESTJ
) {

	public static ReadMbtiResponse from() {
		return new ReadMbtiResponse(
			0,
			0,
			4,
			5,
			0,
			0,
			3,
			10,
			0,
			0,
			2,
			1,
			0,
			0,
			0,
			0
		);
	}
}
