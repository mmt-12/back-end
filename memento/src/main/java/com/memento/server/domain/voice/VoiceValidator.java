package com.memento.server.domain.voice;

import static com.memento.server.common.error.ErrorCodes.VOICE_ASSOCIATE_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.VOICE_NAME_BLANK;
import static com.memento.server.common.error.ErrorCodes.VOICE_NAME_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.VOICE_NAME_TOO_LONG;
import static com.memento.server.common.error.ErrorCodes.VOICE_URL_BLANK;
import static com.memento.server.common.error.ErrorCodes.VOICE_URL_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.VOICE_URL_TOO_LONG;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VoiceValidator {

	private static final int MAX_NAME_LENGTH = 102;
	private static final int MAX_URL_LENGTH = 255;

	public static void validateName(String name) {
		if (name == null) {
			throw new MementoException(VOICE_NAME_REQUIRED);
		}

		if (name.isBlank()) {
			throw new MementoException(VOICE_NAME_BLANK);
		}

		if (name.length() > MAX_NAME_LENGTH) {
			throw new MementoException(VOICE_NAME_TOO_LONG);
		}
	}

	public static void validateUrl(String url) {
		if (url == null) {
			throw new MementoException(VOICE_URL_REQUIRED);
		}

		if (url.isBlank()) {
			throw new MementoException(VOICE_URL_BLANK);
		}

		if (url.length() > MAX_URL_LENGTH) {
			throw new MementoException(VOICE_URL_TOO_LONG);
		}
	}

	public static void validateAssociate(Associate associate) {
		if (associate == null) {
			throw new MementoException(VOICE_ASSOCIATE_REQUIRED);
		}
	}
}
