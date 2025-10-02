package com.memento.server.domain.emoji;

import java.util.List;

import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.response.EmojiResponse;

public interface EmojiRepositoryCustom {

	List<EmojiResponse> findEmojiByCommunityWithCursor(EmojiListQueryRequest request);
}
