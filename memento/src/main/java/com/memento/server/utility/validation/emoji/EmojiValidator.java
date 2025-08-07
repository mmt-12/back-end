package com.memento.server.utility.validation.emoji;

import static com.memento.server.common.error.ErrorCodes.EMOJI_ASSOCIATE_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.EMOJI_NAME_BLANK;
import static com.memento.server.common.error.ErrorCodes.EMOJI_NAME_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.EMOJI_NAME_TOO_LONG;
import static com.memento.server.common.error.ErrorCodes.EMOJI_URL_BLANK;
import static com.memento.server.common.error.ErrorCodes.EMOJI_URL_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.EMOJI_URL_TOO_LONG;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmojiValidator {

	private static final int MAX_NAME_LENGTH = 102;
	private static final int MAX_URL_LENGTH = 255;

	public static void validateName(String name) {
		if (name == null) {
			throw new MementoException(EMOJI_NAME_REQUIRED);
		}

		if (name.isBlank()) {
			throw new MementoException(EMOJI_NAME_BLANK);
		}

		if (name.length() > MAX_NAME_LENGTH) {
			throw new MementoException(EMOJI_NAME_TOO_LONG);
		}
	}

	public static void validateUrl(String url) {
		if (url == null) {
			throw new MementoException(EMOJI_URL_REQUIRED);
		}

		if (url.isBlank()) {
			throw new MementoException(EMOJI_URL_BLANK);
		}

		if (url.length() > MAX_URL_LENGTH) {
			throw new MementoException(EMOJI_URL_TOO_LONG);
		}
	}

	public static void validateAssociate(Associate associate) {
		if (associate == null) {
			throw new MementoException(EMOJI_ASSOCIATE_REQUIRED);
		}
	}
}
