package com.memento.server.utility.validation.achievement;

import static com.memento.server.common.error.ErrorCodes.ACHIEVEMENT_CRITERIA_BLANK;
import static com.memento.server.common.error.ErrorCodes.ACHIEVEMENT_CRITERIA_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.ACHIEVEMENT_CRITERIA_TOO_LONG;
import static com.memento.server.common.error.ErrorCodes.ACHIEVEMENT_NAME_BLANK;
import static com.memento.server.common.error.ErrorCodes.ACHIEVEMENT_NAME_REQUIRED;
import static com.memento.server.common.error.ErrorCodes.ACHIEVEMENT_NAME_TOO_LONG;
import static com.memento.server.common.error.ErrorCodes.ACHIEVEMENT_TYPE_REQUIRED;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.achievement.AchievementType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AchievementValidator {

	private static final int MAX_NAME_LENGTH = 102;
	private static final int MAX_CRITERIA_LENGTH = 255;

	public static void validateName(String name) {
		if (name == null) {
			throw new MementoException(ACHIEVEMENT_NAME_REQUIRED);
		}

		if (name.isBlank()) {
			throw new MementoException(ACHIEVEMENT_NAME_BLANK);
		}

		if (name.length() > MAX_NAME_LENGTH) {
			throw new MementoException(ACHIEVEMENT_NAME_TOO_LONG);
		}
	}

	public static void validateCriteria(String criteria) {
		if (criteria == null) {
			throw new MementoException(ACHIEVEMENT_CRITERIA_REQUIRED);
		}

		if (criteria.isBlank()) {
			throw new MementoException(ACHIEVEMENT_CRITERIA_BLANK);
		}

		if (criteria.length() > MAX_CRITERIA_LENGTH) {
			throw new MementoException(ACHIEVEMENT_CRITERIA_TOO_LONG);
		}
	}

	public static void validateType(AchievementType type) {
		if (type == null) {
			throw new MementoException(ACHIEVEMENT_TYPE_REQUIRED);
		}
	}
}
