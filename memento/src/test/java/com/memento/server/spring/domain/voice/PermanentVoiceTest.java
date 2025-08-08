package com.memento.server.spring.domain.voice;

import static com.memento.server.common.error.ErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.voice.Voice;

public class PermanentVoiceTest {

	private static final String VALID_NAME = "voice";
	private static final String VALID_URL = "https://example.com/audio.wav";
	private static final Associate VALID_ASSOCIATE = AssociateFixtures.associate();

	@Test
	@DisplayName("보이스를 생성한다.")
	void createPermanent() {
		// when
		Voice voice = Voice.createPermanent(VALID_NAME, VALID_URL, VALID_ASSOCIATE);

		// then
		assertThat(voice).isNotNull();
		assertThat(voice.getTemporary()).isFalse();
		assertThat(voice.getName()).isEqualTo(VALID_NAME);
		assertThat(voice.getUrl()).isEqualTo(VALID_URL);
		assertThat(voice.getAssociate()).isEqualTo(VALID_ASSOCIATE);
	}

	@Test
	@DisplayName("보이스 생성 시 이름이 null이면 VOICE_NAME_REQUIRED 예외가 발생한다.")
	void createPermanent_withNullName_throwsException() {
		assertThatThrownBy(() -> Voice.createPermanent(null, VALID_URL, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_NAME_REQUIRED);
	}

	@Test
	@DisplayName("보이스 생성 시 이름이 공백이면 VOICE_NAME_BLANK 예외가 발생한다.")
	void createPermanent_withBlankName_throwsException() {
		assertThatThrownBy(() -> Voice.createPermanent("   ", VALID_URL, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_NAME_BLANK);
	}

	@Test
	@DisplayName("보이스 생성 시 이름이 102자를 초과하면 VOICE_NAME_TOO_LONG 예외가 발생한다.")
	void createPermanent_withTooLongName_throwsException() {
		// given
		String tooLongName = "a".repeat(103);

		// when && then
		assertThatThrownBy(() -> Voice.createPermanent(tooLongName, VALID_URL, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_NAME_TOO_LONG);
	}

	@Test
	@DisplayName("보이스 생성 시 URL이 null이면 VOICE_URL_REQUIRED 예외가 발생한다.")
	void createPermanent_withNullUrl_throwsException() {
		assertThatThrownBy(() -> Voice.createPermanent(VALID_NAME, null, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_URL_REQUIRED);
	}

	@Test
	@DisplayName("보이스 생성 시 URL이 공백이면 VOICE_URL_BLANK 예외가 발생한다.")
	void createPermanent_withBlankUrl_throwsException() {
		assertThatThrownBy(() -> Voice.createPermanent(VALID_NAME, "   ", VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_URL_BLANK);
	}

	@Test
	@DisplayName("보이스 생성 시 URL이 255자를 초과하면 VOICE_URL_TOO_LONG 예외가 발생한다.")
	void createPermanent_withTooLongUrl_throwsException() {
		// given
		String tooLongUrl = "a".repeat(256);

		// when && then
		assertThatThrownBy(() -> Voice.createPermanent(VALID_NAME, tooLongUrl, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_URL_TOO_LONG);
	}

	@Test
	@DisplayName("보이스 생성 시 associate가 null이면 VOICE_ASSOCIATE_REQUIRED 예외가 발생한다.")
	void createPermanent_withNullAssociate_throwsException() {
		assertThatThrownBy(() -> Voice.createTemporary(VALID_URL, null))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_ASSOCIATE_REQUIRED);
	}

	@Test
	@DisplayName("보이스는 isPermanent()가 true이고 isTemporary()가 false이다.")
	void isPermanentVoice() {
		// when
		Voice voice = Voice.createPermanent(VALID_NAME, VALID_URL, VALID_ASSOCIATE);

		// then
		assertThat(voice.isTemporary()).isFalse();
		assertThat(voice.isPermanent()).isTrue();
	}
}
