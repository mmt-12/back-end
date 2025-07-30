package com.memento.server.api.service.voice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.voice.dto.request.VoiceCreateServiceRequest;
import com.memento.server.domain.voice.VoiceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VoiceService {

	private final VoiceRepository voiceRepository;

	public void createVoice(VoiceCreateServiceRequest request) {

	}
}
