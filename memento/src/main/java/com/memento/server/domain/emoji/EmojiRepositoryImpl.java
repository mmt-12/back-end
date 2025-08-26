package com.memento.server.domain.emoji;

import static com.memento.server.domain.community.QAssociate.associate;
import static com.memento.server.domain.emoji.QEmoji.emoji;
import static org.springframework.util.StringUtils.hasText;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.response.QEmojiAuthorResponse;
import com.memento.server.api.service.emoji.dto.response.QEmojiResponse;
import com.memento.server.api.service.emoji.dto.response.EmojiResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmojiRepositoryImpl implements EmojiRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<EmojiResponse> findEmojiByCommunityWithCursor(EmojiListQueryRequest request) {
		return queryFactory
			.select(new QEmojiResponse(
				emoji.id,
				emoji.name,
				emoji.url,
				new QEmojiAuthorResponse(
					associate.id,
					associate.nickname,
					associate.profileImageUrl
				)
			))
			.from(emoji)
			.join(emoji.associate, associate)
			.where(
				eqCommunityId(request.communityId()),
				ltId(request.cursor()),
				likeKeyword(request.keyword()),
				deletedAtIsNull()
			)
			.orderBy(emoji.id.desc())
			.limit(request.size() + 1L)
			.fetch();
	}

	private BooleanExpression eqCommunityId(Long communityId) {
		return associate.community.id.eq(communityId);
	}

	private BooleanExpression ltId(Long cursor) {
		return cursor == null ? null : emoji.id.lt(cursor);
	}

	private BooleanExpression likeKeyword(String keyword) {
		if (!hasText(keyword))
			return null;
		return emoji.name.containsIgnoreCase(keyword.trim());
	}

	private BooleanExpression deletedAtIsNull() {
		return emoji.deletedAt.isNull();
	}
}
