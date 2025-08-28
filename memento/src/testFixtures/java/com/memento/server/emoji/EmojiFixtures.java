package com.memento.server.emoji;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.emoji.Emoji;

public class EmojiFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final AtomicLong nameGenerator = new AtomicLong();
	private static final String URL = "https://example.com/emoji/image.png";

	public static Emoji emoji() {
		return Emoji.builder()
			.id(idGenerator.getAndIncrement())
			.name("emoji" + nameGenerator.getAndIncrement())
			.url(URL)
			.associate(AssociateFixtures.associate())
			.build();
	}

	public static Emoji emoji(Associate associate) {
		return Emoji.builder()
			.name("emoji" + nameGenerator.getAndIncrement())
			.url(URL)
			.associate(associate)
			.build();
	}

	public static Emoji emoji(String name, Associate associate) {
		return Emoji.builder()
			.name(name)
			.url(URL)
			.associate(associate)
			.build();
	}
}
