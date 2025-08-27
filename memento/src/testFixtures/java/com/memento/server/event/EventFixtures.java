package com.memento.server.event;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.event.Event;

public class EventFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";

	public static Event event() {
		return Event.builder()
			.title(TITLE)
			.description(DESCRIPTION)
			.location(LocationFixtures.location())
			.period(PeriodFixtures.period())
			.community(CommunityFixtures.community())
			.associate(AssociateFixtures.associate())
			.build();
	}

	public static Event eventWithCommunityAndAssociate(Community community, Associate associate){
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
