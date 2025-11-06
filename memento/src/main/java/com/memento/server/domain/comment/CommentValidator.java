package com.memento.server.domain.comment;

import static com.memento.server.common.error.ErrorCodes.COMMENT_ASSOCIATE_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.COMMENT_POST_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.COMMENT_URL_BLANK;
import static com.memento.server.common.error.ErrorCodes.COMMENT_URL_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.COMMENT_URL_TOO_LONG;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.post.Post;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CommentValidator {

	private static final int MAX_URL_LENGTH = 255;

	public static void validateUrl(String url) {
		if (url == null) {
			throw new MementoException(COMMENT_URL_REQUIRED);
		}

		if (url.isBlank()) {
			throw new MementoException(COMMENT_URL_BLANK);
		}

		if (url.length() > MAX_URL_LENGTH) {
			throw new MementoException(COMMENT_URL_TOO_LONG);
		}
	}

	public static void validatePost(Post post) {
		if (post == null) {
			throw new MementoException(COMMENT_POST_REQUIRED);
		}
	}

	public static void validateAssociate(Associate associate) {
		if (associate == null) {
			throw new MementoException(COMMENT_ASSOCIATE_REQUIRED);
		}
	}
}
