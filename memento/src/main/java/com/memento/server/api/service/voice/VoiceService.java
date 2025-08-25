package com.memento.server.api.service.voice;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.UNAUTHORIZED_VOICE_ACCESS;
import static com.memento.server.common.error.ErrorCodes.VOICE_NOT_FOUND;

import java.util.List;

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.minio.MinioService;
import com.memento.server.api.service.voice.dto.request.PermanentVoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.TemporaryVoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.common.dto.response.PageInfo;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.voice.Voice;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VoiceService {

	private final VoiceRepository voiceRepository;
	private final AssociateRepository associateRepository;
	private final MinioService minioService;
	private final MinioClient minioClient;
	private final MinioProperties minioProperties;

	public Long createTemporaryVoice(TemporaryVoiceCreateServiceRequest request) {
		return null;
	}

	@Transactional
	public void createPermanentVoice(PermanentVoiceCreateServiceRequest request) {
		Associate associate = associateRepository.findByIdAndDeletedAtIsNull(request.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));

		String url = minioService.createFile(request.voice());
		Voice voice = Voice.createPermanent(request.name(), url, associate);
		voiceRepository.save(voice);
	}

	public VoiceListResponse getVoices(VoiceListQueryRequest request) {
		int pageSize = request.size();
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		boolean hasNext = voices.size() > pageSize;
		List<VoiceResponse> items = hasNext ? voices.subList(0, pageSize) : voices;

		Long nextCursor = (hasNext && !items.isEmpty()) ? items.getLast().id() : null;

		return VoiceListResponse.of(items, PageInfo.of(hasNext, nextCursor));
	}

	@Transactional
	public void removeVoice(VoiceRemoveRequest request) {
		Voice voice = voiceRepository.findByIdAndDeletedAtIsNull(request.voiceId())
			.orElseThrow(() -> new MementoException(VOICE_NOT_FOUND));

		if (!voice.getAssociate().getId().equals(request.associateId())) {
			throw new MementoException(UNAUTHORIZED_VOICE_ACCESS);
		}

		minioService.removeFile(voice.getUrl());
		voice.markDeleted();
	}

	public Voice saveVoice(Associate associate, MultipartFile voice) {
		String bucket = minioProperties.getBucket();
		String baseUrl = minioProperties.getUrl();

		String originalFilename = voice.getOriginalFilename();
		String extension = getExtension(originalFilename);
		String filename = "voice/" + UUID.randomUUID() + "." + extension;
		String expectedContentType = "audio/" + extension;

		try (InputStream inputStream = voice.getInputStream()) {
			long contentLength = voice.getSize();

			minioClient.putObject(
				PutObjectArgs.builder()
					.bucket(bucket)
					.object(filename)
					.stream(inputStream, contentLength, -1)
					.contentType(expectedContentType)
					.build()
			);

		} catch (Exception e) {
			throw new MementoException(ErrorCodes.VOICE_SAVE_FAIL);
		}

		Voice saveVoice = voiceRepository.save(Voice.builder()
			.associate(associate)
			.url(baseUrl + "/" + filename)
			.temporary(true)
			.build());

		return saveVoice;
	}
}
