package com.memento.server.api.controller.memory.dto.response;

import com.memento.server.domain.memory.Period;
import java.time.LocalDateTime;

import com.memento.server.api.service.memory.dto.MemoryItem;

import lombok.Builder;

@Builder
public record PeriodResponse(
	LocalDateTime startTime,
	LocalDateTime endTime
) {

	public static PeriodResponse from(Period period) {
		return PeriodResponse.builder()
			.startTime(period.getStartTime())
			.endTime(period.getEndTime())
			.build();
	}

	public static PeriodResponse from(MemoryItem.PeriodDto period) {
		return PeriodResponse.builder()
			.startTime(period.startTime())
			.endTime(period.endTime())
			.build();
	}
}
