package com.memento.server.api.service.mbti.dto;

public record MbtiSearchDto(
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
	public MbtiSearchDto {
		INFP = INFP != null ? INFP : 0L;
		INFJ = INFJ != null ? INFJ : 0L;
		INTP = INTP != null ? INTP : 0L;
		INTJ = INTJ != null ? INTJ : 0L;
		ISFP = ISFP != null ? ISFP : 0L;
		ISFJ = ISFJ != null ? ISFJ : 0L;
		ISTP = ISTP != null ? ISTP : 0L;
		ISTJ = ISTJ != null ? ISTJ : 0L;
		ENFP = ENFP != null ? ENFP : 0L;
		ENFJ = ENFJ != null ? ENFJ : 0L;
		ENTP = ENTP != null ? ENTP : 0L;
		ENTJ = ENTJ != null ? ENTJ : 0L;
		ESFP = ESFP != null ? ESFP : 0L;
		ESFJ = ESFJ != null ? ESFJ : 0L;
		ESTP = ESTP != null ? ESTP : 0L;
		ESTJ = ESTJ != null ? ESTJ : 0L;
	}
}
