package com.memento.server.api.controller.community.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record SearchAssociateResponse(
	String nickname,
	Achievement achievement,
	String imageUrl,
	String introduction,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate birthday
) {
	@Getter
	@Builder
	@AllArgsConstructor
	public static class Achievement {
		Long id;
		String name;
	}
}
