package com.memento.server.api.controller.memory.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.Location;
import com.memento.server.domain.event.Period;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.post.PostImage;

import lombok.Builder;

@Builder
public record ReadMemoryResponse(
	Long id,
	String title,
	String description,
	PeriodResponse period,
	LocationResponse location,
	Integer memberAmount,
	Integer pictureAmount,
	List<String> pictures
) {
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
	}

	@Builder
	public record LocationResponse(
		String address,
		String name,
		Float latitude,
		Float longitude,
		Integer code
	) {
		public static LocationResponse from(Location location) {
			return LocationResponse.builder()
				.address(location.getAddress())
				.name(location.getName())
				.latitude(location.getLatitude().floatValue())
				.longitude(location.getLongitude().floatValue())
				.code(location.getCode())
				.build();
		}
	}

	public static ReadMemoryResponse from(
		Memory memory,
		List<PostImage> images,
		Long associateCount
	) {
		Event event = memory.getEvent();
		List<String> pictures = new ArrayList<>();

		for (PostImage image : images) {
			pictures.add(image.getUrl());
			if (pictures.size() >= 9)
				break;
		}

		return ReadMemoryResponse.builder()
			.id(memory.getId())
			.title(event.getTitle())
			.description(event.getDescription())
			.period(PeriodResponse.from(event.getPeriod()))
			.location(LocationResponse.from(event.getLocation()))
			.memberAmount(Math.toIntExact(associateCount))
			.pictureAmount(images.size())
			.pictures(pictures)
			.build();
	}
}
