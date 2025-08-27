package com.memento.server.domain.voice;

import java.util.List;

import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;

public interface VoiceRepositoryCustom {

	List<VoiceResponse> findVoicesByCommunityWithCursor(VoiceListQueryRequest request);
}