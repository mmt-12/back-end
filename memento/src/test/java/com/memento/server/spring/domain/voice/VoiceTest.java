package com.memento.server.spring.domain.voice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.voice.Voice;
import com.memento.server.fixture.associate.AssociateFixtures;

public class VoiceTest {

	@Test
	@DisplayName("일회용 보이스를 생성한다.")
	void createTemporary() {
		// given
		String name = "인쥐용";
		String url = "https://example.com/audio.wav";
		Associate associate = AssociateFixtures.associate();

		// when
		Voice voice = Voice.createTemporary(name, url, associate);

		// then
		assertThat(voice.getIsTemporary()).isTrue();
		assertThat(voice.getName()).isEqualTo(name);
		assertThat(voice.getUrl()).isEqualTo(url);
		assertThat(voice.getAssociate()).isEqualTo(associate);
	}

	@Test
	@DisplayName("등록될 보이스를 생성한다.")
	void createPermanent() {
		// given
		String name = "인쥐용";
		String url = "https://example.com/audio.wav";
		Associate associate = AssociateFixtures.associate();

		// when
		Voice voice = Voice.createPermanent(name, url, associate);

		// then
		assertThat(voice.getIsTemporary()).isFalse();
		assertThat(voice.getName()).isEqualTo(name);
		assertThat(voice.getUrl()).isEqualTo(url);
		assertThat(voice.getAssociate()).isEqualTo(associate);
	}

	@Test
	@DisplayName("임시 보이스는 isTemporary()가 true이고 isPermanent()가 false이다.")
	void isTemporaryVoice() {
		// given
		String name = "인쥐용";
		String url = "https://example.com/audio.wav";
		Associate associate = AssociateFixtures.associate();

		// when
		Voice voice = Voice.createTemporary(name, url, associate);

		// then
		assertThat(voice.isTemporary()).isTrue();
		assertThat(voice.isPermanent()).isFalse();
	}

	@Test
	@DisplayName("영구 보이스는 isPermanent()가 true이고 isTemporary()가 false이다.")
	void isPermanentVoice() {
		// given
		String name = "인쥐용";
		String url = "https://example.com/audio.wav";
		Associate associate = AssociateFixtures.associate();

		// when
		Voice voice = Voice.createPermanent(name, url, associate);

		// then
		assertThat(voice.isTemporary()).isFalse();
		assertThat(voice.isPermanent()).isTrue();
	}
}
