package com.memento.server.spring.domain.voice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.voice.Voice;

public class VoiceTest {

	private static final String NAME = "voice";
	private static final String URL = "https://example.com/audio.wav";
	private static final Associate DEFAULT_ASSOCIATE = AssociateFixtures.associate();

	@Test
	@DisplayName("일회용 보이스를 생성한다.")
	void createTemporary() {
		// when
		Voice voice = Voice.createTemporary(NAME, URL, DEFAULT_ASSOCIATE);

		// then
		assertThat(voice.getIsTemporary()).isTrue();
		assertThat(voice.getName()).isEqualTo(NAME);
		assertThat(voice.getUrl()).isEqualTo(URL);
		assertThat(voice.getAssociate()).isEqualTo(DEFAULT_ASSOCIATE);
	}

	@Test
	@DisplayName("등록될 보이스를 생성한다.")
	void createPermanent() {
		// when
		Voice voice = Voice.createPermanent(NAME, URL, DEFAULT_ASSOCIATE);

		// then
		assertThat(voice.getIsTemporary()).isFalse();
		assertThat(voice.getName()).isEqualTo(NAME);
		assertThat(voice.getUrl()).isEqualTo(URL);
		assertThat(voice.getAssociate()).isEqualTo(DEFAULT_ASSOCIATE);
	}

	@Test
	@DisplayName("임시 보이스는 isTemporary()가 true이고 isPermanent()가 false이다.")
	void isTemporaryVoice() {
		// when
		Voice voice = Voice.createTemporary(NAME, URL, DEFAULT_ASSOCIATE);

		// then
		assertThat(voice.isTemporary()).isTrue();
		assertThat(voice.isPermanent()).isFalse();
	}

	@Test
	@DisplayName("영구 보이스는 isPermanent()가 true이고 isTemporary()가 false이다.")
	void isPermanentVoice() {
		// when
		Voice voice = Voice.createPermanent(NAME, URL, DEFAULT_ASSOCIATE);

		// then
		assertThat(voice.isTemporary()).isFalse();
		assertThat(voice.isPermanent()).isTrue();
	}
}
