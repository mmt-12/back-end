package com.memento.server.domain.voice;

import java.util.List;

import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;

public interface VoiceRepositoryCustom {
	
	List<Voice> findVoicesWithPagination(VoiceListQueryRequest request);
	
	boolean hasNextVoice(VoiceListQueryRequest request, Long lastVoiceId);
}