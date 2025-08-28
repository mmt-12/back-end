package com.memento.server.post;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.post.Post;

public class PostFixtures {

	private static final String CONTENT = "content";

	public static Post post(Memory memory, Associate associate) {
		return Post.builder()
			.content(CONTENT)
			.memory(memory)
			.associate(associate)
			.build();
	}
}
