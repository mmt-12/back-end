package com.memento.server.api.controller.memory.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record ReadAllMemoryResponse(
	Long cursor,
	Boolean hasNext,
	List<Memory> memories
) {

	@Builder
	public record Memory(
		Long id,
		String title,
		String description,
		Period period,
		Location location,
		Integer memberAmount,
		Integer pictureAmount,
		List<String> pictures
	) {

		@Builder
		public record Period(
			LocalDateTime startDate,
			LocalDateTime endDate
		) {
		}

		@Builder
		public record Location(
			String address,
			String name,
			Float latitude,
			Float longitude,
			String code
		) {
		}
	}
}
