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

public class TemporaryVoiceTest {

	private static final String VALID_URL = "https://example.com/audio.wav";
	private static final Associate VALID_ASSOCIATE = AssociateFixtures.associate();

	@Test
	@DisplayName("일회용 보이스를 생성한다.")
	void createTemporary() {
		// when
		Voice voice = Voice.createTemporary(VALID_URL, VALID_ASSOCIATE);

		// then
		assertThat(voice).isNotNull();
		assertThat(voice.getTemporary()).isTrue();
		assertThat(voice.getUrl()).isEqualTo(VALID_URL);
		assertThat(voice.getAssociate()).isEqualTo(VALID_ASSOCIATE);
	}

	@Test
	@DisplayName("일회용 보이스 생성 시 URL이 null이면 VOICE_URL_REQUIRED 예외가 발생한다.")
	void createTemporary_withNullUrl_throwsException() {
		assertThatThrownBy(() -> Voice.createTemporary(null, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_URL_REQUIRED);
	}

	@Test
	@DisplayName("일회용 보이스 생성 시 URL이 공백이면 VOICE_URL_BLANK 예외가 발생한다.")
	void createTemporary_withBlankUrl_throwsException() {
		assertThatThrownBy(() -> Voice.createTemporary("   ", VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_URL_BLANK);
	}

	@Test
	@DisplayName("일회용 보이스 생성 시 URL이 255자를 초과하면 VOICE_URL_TOO_LONG 예외가 발생한다.")
	void createTemporary_withTooLongUrl_throwsException() {
		// given
		String tooLongUrl = "a".repeat(256);

		// when && then
		assertThatThrownBy(() -> Voice.createTemporary(tooLongUrl, VALID_ASSOCIATE))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_URL_TOO_LONG);
	}

	@Test
	@DisplayName("일회용 보이스 생성 시 associate가 null이면 VOICE_ASSOCIATE_REQUIRED 예외가 발생한다.")
	void createTemporary_withNullAssociate_throwsException() {
		assertThatThrownBy(() -> Voice.createTemporary(VALID_URL, null))
			.isInstanceOf(MementoException.class)
			.hasFieldOrPropertyWithValue("errorCode", VOICE_ASSOCIATE_REQUIRED);
	}

	@Test
	@DisplayName("일회용 보이스는 isTemporary()가 true이고 isPermanent()가 false이다.")
	void isTemporaryVoice() {
		// when
		Voice voice = Voice.createTemporary(VALID_URL, VALID_ASSOCIATE);

		// then
		assertThat(voice.isTemporary()).isTrue();
		assertThat(voice.isPermanent()).isFalse();
	}
}
