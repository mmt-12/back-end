package com.memento.server.api.controller.memory.dto.request;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record ReadMemoryListRequest(
	Long cursor,
	Integer size,
	String keyword,
	LocalDate startTime,
	LocalDate endTime
) {
}
