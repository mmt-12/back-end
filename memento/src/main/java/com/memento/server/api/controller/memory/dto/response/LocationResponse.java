package com.memento.server.api.controller.memory.dto.response;

import com.memento.server.api.service.memory.dto.MemoryItem;

import com.memento.server.domain.memory.Location;
import lombok.Builder;

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

	public static LocationResponse from(MemoryItem.LocationDto location) {
		return LocationResponse.builder()
			.address(location.address())
			.name(location.name())
			.latitude(location.latitude().floatValue())
			.longitude(location.longitude().floatValue())
			.code(location.code())
			.build();
	}
}
