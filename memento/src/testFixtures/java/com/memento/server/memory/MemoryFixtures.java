package com.memento.server.memory;

import com.memento.server.domain.event.Event;
import com.memento.server.domain.memory.Memory;

public class MemoryFixtures {

	public static Memory memory(Event event) {
		return Memory.builder()
			.event(event)
			.build();
	}
}
