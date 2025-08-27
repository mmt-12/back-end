package com.memento.server.api.service.emoji;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.EMOJI_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.UNAUTHORIZED_EMOJI_ACCESS;
import static com.memento.server.config.MinioProperties.FileType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.minio.MinioService;
import com.memento.server.api.service.emoji.dto.request.EmojiCreateServiceRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiRemoveRequest;
import com.memento.server.common.dto.response.PageInfo;
import com.memento.server.api.service.emoji.dto.response.EmojiListResponse;
import com.memento.server.api.service.emoji.dto.response.EmojiResponse;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EmojiService {

	private final EmojiRepository emojiRepository;
	private final AssociateRepository associateRepository;
	private final MinioService minioService;

	@Transactional
	public void createEmoji(EmojiCreateServiceRequest request) {
		Associate associate = associateRepository.findByIdAndDeletedAtIsNull(request.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));

		String url = minioService.createFile(request.emoji(), EMOJI);
		Emoji emoji = Emoji.create(request.name(), url, associate);
		emojiRepository.save(emoji);
	}

	public EmojiListResponse getEmoji(EmojiListQueryRequest request) {
		int pageSize = request.size();
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		boolean hasNext = emojis.size() > pageSize;
		List<EmojiResponse> items = hasNext ? emojis.subList(0, pageSize) : emojis;

		Long nextCursor = (hasNext && !items.isEmpty()) ? items.getLast().id() : null;

		return EmojiListResponse.of(items, PageInfo.of(hasNext, nextCursor));
	}

	@Transactional
	public void removeEmoji(EmojiRemoveRequest request) {
		Emoji emoji = emojiRepository.findByIdAndDeletedAtIsNull(request.emojiId())
			.orElseThrow(() -> new MementoException(EMOJI_NOT_FOUND));

		if (!emoji.getAssociate().getId().equals(request.associateId())) {
			throw new MementoException(UNAUTHORIZED_EMOJI_ACCESS);
		}

		minioService.removeFile(emoji.getUrl());
		emoji.markDeleted();
	}
}
