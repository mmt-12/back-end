package com.memento.server.api.controller.memory.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record CreateUpdateMemoryRequest(
	String title,
	PeriodRequest period,
	String description,
	List<Long> associates,
	LocationRequest location
) {

	@Builder
	public record PeriodRequest(
		LocalDateTime startTime,
		LocalDateTime endTime
	) {
	}

	@Builder
	public record LocationRequest(
		Float latitude,
		Float longitude,
		Integer code,
		String name,
		String address
	) {
	}
}
