package com.memento.server.memory;

import java.math.BigDecimal;

import com.memento.server.domain.memory.Location;

public class LocationFixtures {

	private static final BigDecimal LATITUDE = BigDecimal.valueOf(37.4979);
	private static final BigDecimal LONGITUDE = BigDecimal.valueOf(127.0276);
	private static final Integer CODE = 11680;
	private static final String NAME = "강남구청";
	private static final String ADDRESS = "서울특별시 강남구 학동로 426";

	public static Location location() {
		return Location.builder()
			.latitude(LATITUDE)
			.longitude(LONGITUDE)
			.code(CODE)
			.name(NAME)
			.address(ADDRESS)
			.build();
	}
}
