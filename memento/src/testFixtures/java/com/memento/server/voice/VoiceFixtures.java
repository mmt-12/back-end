package com.memento.server.voice;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.domain.voice.Voice;

public class VoiceFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final String NAME = "voice";
	private static final String URL = "https://example.com/audio.wav";

	public static Voice temporaryVoice() {
		return Voice.builder()
			.id(idGenerator.getAndIncrement())
			.name(NAME)
			.url(URL)
			.isTemporary(true)
			.associate(AssociateFixtures.associate())
			.build();
	}

	public static Voice permanentVoice() {
		return Voice.builder()
			.id(idGenerator.getAndIncrement())
			.name(NAME)
			.url(URL)
			.isTemporary(false)
			.associate(AssociateFixtures.associate())
			.build();
	}
}
