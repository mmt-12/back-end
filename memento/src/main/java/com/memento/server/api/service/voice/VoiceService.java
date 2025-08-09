package com.memento.server.api.service.voice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.voice.dto.request.PermanentVoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.TemporaryVoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.domain.voice.VoiceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VoiceService {

	private final VoiceRepository voiceRepository;

	public Long createTemporaryVoice(TemporaryVoiceCreateServiceRequest request) {
		return null;
	}

	public void createPermanentVoice(PermanentVoiceCreateServiceRequest request) {

	}

	public VoiceListResponse getVoices(VoiceListQueryRequest request) {
		return null;
	}

	public void removeVoice(VoiceRemoveRequest request) {

	}
}
