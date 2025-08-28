package com.memento.server.event;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.event.Event;

public class EventFixtures {

	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";

	public static Event event(Community community, Associate associate) {
		return Event.builder()
			.title(TITLE)
			.description(DESCRIPTION)
			.location(LocationFixtures.location())
			.period(PeriodFixtures.period())
			.community(community)
			.associate(associate)
			.build();
	}
}
