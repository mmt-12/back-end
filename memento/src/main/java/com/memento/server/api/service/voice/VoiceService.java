package com.memento.server.api.service.voice;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.minio.MinioService;
import com.memento.server.api.service.voice.dto.request.PermanentVoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.TemporaryVoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VoiceService {

	private final VoiceRepository voiceRepository;
	private final AssociateRepository associateRepository;
	private final MinioService minioService;

	public Long createTemporaryVoice(TemporaryVoiceCreateServiceRequest request) {
		return null;
	}

	@Transactional
	public void createPermanentVoice(PermanentVoiceCreateServiceRequest request) {
		String url = minioService.createPermanentVoice(request.voice());
		Associate associate = associateRepository.findById(request.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));
		Voice voice = Voice.createPermanent(request.name(), url, associate);
		voiceRepository.save(voice);
	}

	public VoiceListResponse getVoices(VoiceListQueryRequest request) {
		List<Voice> voices = voiceRepository.findVoicesWithPagination(request);
		
		// hasNext 판단을 위해 size + 1로 조회했으므로 실제 데이터는 size만큼만 사용
		boolean hasNext = voices.size() > request.size();
		if (hasNext) {
			voices = voices.subList(0, request.size());
		}
		
		// Voice 엔티티를 VoiceResponse로 변환
		List<VoiceResponse> voiceResponses = voices.stream()
			.map(VoiceResponse::of)
			.toList();
		
		// 다음 커서 계산 (마지막 보이스의 ID)
		Long nextCursor = voices.isEmpty() ? null : voices.get(voices.size() - 1).getId();
		
		return VoiceListResponse.of(
			voiceResponses,
			request.cursor(),
			request.size(),
			nextCursor,
			hasNext
		);
	}

	public void removeVoice(VoiceRemoveRequest request) {

	}
}
