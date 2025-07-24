package com.memento.server.api.controller.community.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record ReadAssociateResponse(
	String nickname,
	Achievement achievement,
	String imageUrl,
	String introduction,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate birthday
) {

	public static ReadAssociateResponse from() {
		Achievement achievement = new Achievement(1L, "뤼전드");
		return new ReadAssociateResponse(
			"오큰수",
			achievement,
			"www.example.com/ohjs",
			"싱싱싱~ 팅!팅!팅! 아!다 막았죠! 인지용~?",
			LocalDate.of(1999, 10, 13)
		);
	}

	@Getter
	@Builder
	@AllArgsConstructor
	public static class Achievement {
		Long id;
		String name;
	}
}
