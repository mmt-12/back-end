package com.memento.server.post;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.domain.post.Post;
import com.memento.server.memory.MemoryFixtures;

public class PostFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String CONTENT = "content";

	public static Post post() {
		return Post.builder()
			.id(idGenerator.getAndIncrement())
			.content(CONTENT)
			.memory(MemoryFixtures.memory())
			.associate(AssociateFixtures.associate())
			.build();
	}
}
