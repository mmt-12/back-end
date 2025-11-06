package com.memento.server.memory;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.memory.Memory;

public class MemoryFixtures {

	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";

	public static Memory memory(Community community, Associate associate) {
		return Memory.builder()
			.title(TITLE)
			.description(DESCRIPTION)
			.location(LocationFixtures.location())
			.period(PeriodFixtures.period())
			.community(community)
			.associate(associate)
			.build();
	}
}
