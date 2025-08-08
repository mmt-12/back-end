package com.memento.server.api.service.emoji;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.emoji.dto.request.EmojiCreateServiceRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiRemoveRequest;
import com.memento.server.api.service.emoji.dto.response.EmojiListResponse;
import com.memento.server.domain.emoji.EmojiRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class EmojiService {

	private final EmojiRepository emojiRepository;

	public void createEmoji(EmojiCreateServiceRequest request) {

	}

	public EmojiListResponse getEmoji(EmojiListQueryRequest request) {

		return null;
	}

	public void removeEmoji(EmojiRemoveRequest request) {

	}
}
