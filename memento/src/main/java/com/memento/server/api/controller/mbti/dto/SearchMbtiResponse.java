package com.memento.server.api.controller.mbti.dto;

import lombok.Builder;

@Builder
public record SearchMbtiResponse(
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
	public static SearchMbtiResponse from(Object[] counts) {
		if (counts == null || counts.length != 16) {
			throw new IllegalStateException("MBTI count 결과가 올바르지 않습니다.");
		}

		return SearchMbtiResponse.builder()
			.INFP(((Number) counts[0]).intValue())
			.INFJ(((Number) counts[1]).intValue())
			.INTP(((Number) counts[2]).intValue())
			.INTJ(((Number) counts[3]).intValue())
			.ISFP(((Number) counts[4]).intValue())
			.ISFJ(((Number) counts[5]).intValue())
			.ISTP(((Number) counts[6]).intValue())
			.ISTJ(((Number) counts[7]).intValue())
			.ENFP(((Number) counts[8]).intValue())
			.ENFJ(((Number) counts[9]).intValue())
			.ENTP(((Number) counts[10]).intValue())
			.ENTJ(((Number) counts[11]).intValue())
			.ESFP(((Number) counts[12]).intValue())
			.ESFJ(((Number) counts[13]).intValue())
			.ESTP(((Number) counts[14]).intValue())
			.ESTJ(((Number) counts[15]).intValue())
			.build();
	}
}
