package com.memento.server.api.controller.memory.dto;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record ReadAllMemoryRequest(
	Long cursor,
	Long size,
	String keyword,
	LocalDate startDate,
	LocalDate endDate
) {
}
