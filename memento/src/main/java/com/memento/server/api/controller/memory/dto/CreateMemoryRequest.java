package com.memento.server.api.controller.memory.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record CreateMemoryRequest(
	String title,
	Period period,
	String description,
	List<Long> associates,
	Location location
) {

	@Builder
	public record Period(
		LocalDateTime startTime,
		LocalDateTime endTime
	) {
	}

	@Builder
	public record Location(
		Float latitude,
		Float longitude,
		String code,
		String name,
		String address
	) {
	}
}
