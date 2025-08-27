package com.memento.server.comment;

import static com.memento.server.domain.comment.CommentType.*;

import com.memento.server.domain.comment.Comment;
import com.memento.server.domain.comment.CommentType;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.voice.Voice;

public class CommentFixtures {

	private static final String URL = "https://example.com/image.png";
	private static final CommentType TYPE = EMOJI;

	public static Comment comment(Post post, Associate associate) {
		return Comment.builder()
			.url(URL)
			.type(TYPE)
			.post(post)
			.associate(associate)
			.build();
	}

	public static Comment emojiComment(Emoji emoji, Post post, Associate associate) {
		return Comment.builder()
			.url(emoji.getUrl())
			.type(EMOJI)
			.post(post)
			.associate(associate)
			.build();
	}

	public static Comment temporaryVoiceComment(Voice voice, Post post, Associate associate) {
		return Comment.builder()
			.url(voice.getUrl())
			.type(VOICE)
			.post(post)
			.associate(associate)
			.build();
	}

	public static Comment permanentVoiceComment(Voice voice, Post post, Associate associate) {
		return Comment.builder()
			.url(voice.getUrl())
			.type(VOICE)
			.post(post)
			.associate(associate)
			.build();
	}
}
