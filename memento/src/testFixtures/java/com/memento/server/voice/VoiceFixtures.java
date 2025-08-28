package com.memento.server.voice;

import java.util.concurrent.atomic.AtomicLong;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.voice.Voice;

public class VoiceFixtures {

	private static final AtomicLong idGenerator = new AtomicLong();
	private static final AtomicLong nameGenerator = new AtomicLong();
	private static final String TEMPORARY_URL = "https://example.com/voice/temporary.wav";
	private static final String PERMANENT_URL = "https://example.com/voice/permanent.wav";

	public static Voice temporaryVoice(Associate associate) {
		return Voice.builder()
			.name(null)
			.url(TEMPORARY_URL)
			.temporary(true)
			.associate(associate)
			.build();
	}

	public static Voice temporaryVoice(String url, Associate associate) {
		return Voice.builder()
			.name(null)
			.url(url)
			.temporary(true)
			.associate(associate)
			.build();
	}

	public static Voice permanentVoice() {
		return Voice.builder()
			.id(idGenerator.getAndIncrement())
			.name("voice" + nameGenerator.getAndIncrement())
			.url(PERMANENT_URL)
			.temporary(false)
			.associate(AssociateFixtures.associate())
			.build();
	}

	public static Voice permanentVoice(Associate associate) {
		return Voice.builder()
			.name("voice" + nameGenerator.getAndIncrement())
			.url(PERMANENT_URL)
			.temporary(false)
			.associate(associate)
			.build();
	}

	public static Voice permanentVoice(String url, Associate associate) {
		return Voice.builder()
			.name("voice" + nameGenerator.getAndIncrement())
			.url(url)
			.temporary(false)
			.associate(associate)
			.build();
	}

	public static Voice permanentVoice(String name, String url, Associate associate) {
		return Voice.builder()
			.name(name)
			.url(url)
			.temporary(false)
			.associate(associate)
			.build();
	}
}
