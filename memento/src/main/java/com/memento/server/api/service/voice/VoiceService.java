package com.memento.server.api.service.voice;

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.voice.dto.request.VoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
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
	private final MinioClient minioClient;
	private final MinioProperties minioProperties;

	public void createVoice(VoiceCreateServiceRequest request) {

	}

	public VoiceListResponse getVoices(VoiceListQueryRequest request) {
		return null;
	}

	public void removeVoice(VoiceRemoveRequest request) {

	}

	public Long saveVoice(Associate associate, MultipartFile voice) {
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

		return saveVoice.getId();
	}
}
