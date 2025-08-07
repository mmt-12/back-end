package com.memento.server.emoji;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.domain.emoji.Emoji;

public class EmojiFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String NAME = "emoji";
	private static final String URL = "https://example.com/image.png";

	public static Emoji emoji() {
		return Emoji.builder()
			.id(idGenerator.getAndIncrement())
			.name(NAME)
			.url(URL)
			.associate(AssociateFixtures.associate())
			.build();
	}
}
