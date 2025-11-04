package com.memento.server.memory;

import java.time.LocalDateTime;

import com.memento.server.domain.memory.Period;

public class PeriodFixtures {

	private static final LocalDateTime START_TIME = LocalDateTime.of(2018, 1, 1, 0, 0);
	private static final LocalDateTime END_TIME = START_TIME.plusDays(1);

	public static Period period() {
		return Period.builder().startTime(START_TIME).endTime(END_TIME).build();
	}
}