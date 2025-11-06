package com.memento.server.api.service.mbti.dto.response;

import com.memento.server.api.service.mbti.dto.MbtiSearchDto;

import lombok.Builder;

@Builder
public record SearchMbtiResponse(
	Long INFP,
	Long INFJ,
	Long INTP,
	Long INTJ,
	Long ISFP,
	Long ISFJ,
	Long ISTP,
	Long ISTJ,
	Long ENFP,
	Long ENFJ,
	Long ENTP,
	Long ENTJ,
	Long ESFP,
	Long ESFJ,
	Long ESTP,
	Long ESTJ
) {
	public static SearchMbtiResponse from(MbtiSearchDto counts) {
		if (counts == null) {
			throw new IllegalStateException("MBTI count 결과가 올바르지 않습니다.");
		}

		return SearchMbtiResponse.builder()
			.INFP(counts.INFP())
			.INFJ(counts.INFJ())
			.INTP(counts.INTP())
			.INTJ(counts.INTJ())
			.ISFP(counts.ISFP())
			.ISFJ(counts.ISFJ())
			.ISTP(counts.ISTP())
			.ISTJ(counts.ISTJ())
			.ENFP(counts.ENFP())
			.ENFJ(counts.ENFJ())
			.ENTP(counts.ENTP())
			.ENTJ(counts.ENTJ())
			.ESFP(counts.ESFP())
			.ESFJ(counts.ESFJ())
			.ESTP(counts.ESTP())
			.ESTJ(counts.ESTJ())
			.build();
	}
}
