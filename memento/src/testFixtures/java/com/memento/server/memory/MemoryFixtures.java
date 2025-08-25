package com.memento.server.memory;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.domain.memory.Memory;
import com.memento.server.event.EventFixtures;

public class MemoryFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();

	public static Memory memory() {
		return Memory.builder()
			.id(idGenerator.getAndIncrement())
			.event(EventFixtures.event())
			.build();
	}
}
