package com.memento.server.spring.domain.achievement;

import static com.memento.server.common.error.ErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.achievement.AchievementType;

public class AchievementTest {

	private static final String VALID_NAME = "연속 출석 달성";
	private static final String VALID_CRITERIA = "7일 연속 출석 시 달성";
	private static final AchievementType VALID_TYPE = AchievementType.OPEN;

	@Test
	@DisplayName("업적을 생성한다.")
	void create() {
		// when
		Achievement achievement = Achievement.create(VALID_NAME, VALID_CRITERIA, VALID_TYPE);

		// then
		assertThat(achievement).isNotNull();
		assertThat(achievement.getName()).isEqualTo(VALID_NAME);
		assertThat(achievement.getCriteria()).isEqualTo(VALID_CRITERIA);
		assertThat(achievement.getType()).isEqualTo(VALID_TYPE);
	}

	@Test
	@DisplayName("업적 생성 시 이름이 null이면 ACHIEVEMENT_NAME_REQUIRED 예외가 발생한다.")
	void createAchievement_withNullName_throwsException() {
		assertThatThrownBy(() -> Achievement.create(null, VALID_CRITERIA, VALID_TYPE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ACHIEVEMENT_NAME_REQUIRED);
	}

	@Test
	@DisplayName("업적 생성 시 이름이 공백이면 ACHIEVEMENT_NAME_BLANK 예외가 발생한다.")
	void createAchievement_withBlankName_throwsException() {
		assertThatThrownBy(() -> Achievement.create("   ", VALID_CRITERIA, VALID_TYPE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ACHIEVEMENT_NAME_BLANK);
	}

	@Test
	@DisplayName("업적 생성 시 이름이 102자를 초과하면 ACHIEVEMENT_NAME_TOO_LONG 예외가 발생한다.")
	void createAchievement_withTooLongName_throwsException() {
		// given
		String tooLongName = "a".repeat(103);

		// when && then
		assertThatThrownBy(() -> Achievement.create(tooLongName, VALID_CRITERIA, VALID_TYPE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ACHIEVEMENT_NAME_TOO_LONG);
	}

	@Test
	@DisplayName("업적 생성 시 기준(criteria)이 null이면 ACHIEVEMENT_CRITERIA_REQUIRED 예외가 발생한다.")
	void createAchievement_withNullCriteria_throwsException() {
		assertThatThrownBy(() -> Achievement.create(VALID_NAME, null, VALID_TYPE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ACHIEVEMENT_CRITERIA_REQUIRED);
	}

	@Test
	@DisplayName("업적 생성 시 기준(criteria)이 공백이면 ACHIEVEMENT_CRITERIA_BLANK 예외가 발생한다.")
	void createAchievement_withBlankCriteria_throwsException() {
		assertThatThrownBy(() -> Achievement.create(VALID_NAME, "   ", VALID_TYPE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ACHIEVEMENT_CRITERIA_BLANK);
	}

	@Test
	@DisplayName("업적 생성 시 기준(criteria)이 255자를 초과하면 ACHIEVEMENT_CRITERIA_TOO_LONG 예외가 발생한다.")
	void createAchievement_withTooLongCriteria_throwsException() {
		// given
		String tooLongCriteria = "a".repeat(256);

		// when && then
		assertThatThrownBy(() -> Achievement.create(VALID_NAME, tooLongCriteria, VALID_TYPE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ACHIEVEMENT_CRITERIA_TOO_LONG);
	}

	@Test
	@DisplayName("업적 생성 시 타입이 null이면 ACHIEVEMENT_TYPE_REQUIRED 예외가 발생한다.")
	void createAchievement_withNullType_throwsException() {
		assertThatThrownBy(() -> Achievement.create(VALID_NAME, VALID_CRITERIA, null))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", ACHIEVEMENT_TYPE_REQUIRED);
	}
}