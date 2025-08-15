package com.memento.server.event;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.event.Event;

public class EventFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";

	public static Event event() {
		return Event.builder()
			.id(idGenerator.getAndIncrement())
			.title(TITLE)
			.description(DESCRIPTION)
			.location(LocationFixtures.location())
			.period(PeriodFixtures.period())
			.community(CommunityFixtures.community())
			.associate(AssociateFixtures.associate())
			.build();
	}
}
