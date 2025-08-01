package com.memento.server.fixture.voice;

import java.util.List;

import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.memento.server.domain.voice.Voice;
import com.memento.server.fixture.associate.AssociateFixtures;

public class VoiceFixtures {

	public static Voice temporaryVoice() {
		return Voice.createTemporary("인쥐용", "https://example.com/audio.wav",
			AssociateFixtures.associate());
	}

	public static Voice permanentVoice() {
		return Voice.createTemporary("인쥐용", "https://example.com/audio.wav",
			AssociateFixtures.associate());
	}

	public static VoiceResponse voiceResponse() {
		return VoiceResponse.of(VoiceFixtures.permanentVoice());
	}

	public static VoiceListResponse voiceListResponse(Long cursor, int size, Long nextCursor, boolean hasNext) {
		return VoiceListResponse.of(List.of(voiceResponse()), cursor, size, nextCursor, hasNext);
	}
}
