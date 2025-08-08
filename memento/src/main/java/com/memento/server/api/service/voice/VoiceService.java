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


	private final String bucket = minioProperties.getBucket();
	private final String baseUrl = minioProperties.getUrl();


	public void createVoice(VoiceCreateServiceRequest request) {

	}

	public VoiceListResponse getVoices(VoiceListQueryRequest request) {
		return null;
	}

	public void removeVoice(VoiceRemoveRequest request) {

	}

	public Long saveVoice(Associate associate, MultipartFile voice) {

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
			throw new RuntimeException("음성 파일 저장에 실패하였습니다.", e);
		}

		Voice saveVoice = voiceRepository.save(Voice.builder()
			.associate(associate)
			.url(baseUrl + "/" + filename)
			.isTemporary(true)
			.build());

		return saveVoice.getId();
	}
}
