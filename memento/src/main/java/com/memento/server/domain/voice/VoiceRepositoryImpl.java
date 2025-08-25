package com.memento.server.domain.voice;

import static com.memento.server.domain.community.QAssociate.associate;
import static com.memento.server.domain.voice.QVoice.voice;
import static org.springframework.util.StringUtils.hasText;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.response.QVoiceAuthorResponse;
import com.memento.server.api.service.voice.dto.response.QVoiceResponse;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VoiceRepositoryImpl implements VoiceRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<VoiceResponse> findVoicesByCommunityWithCursor(VoiceListQueryRequest request) {
		return queryFactory
			.select(new QVoiceResponse(
				voice.id,
				voice.name,
				voice.url,
				new QVoiceAuthorResponse(
					associate.id,
					associate.nickname,
					associate.profileImageUrl
				)
			))
			.from(voice)
			.join(voice.associate, associate)
			.where(
				eqCommunityId(request.communityId()),
				ltId(request.cursor()),
				likeKeyword(request.keyword()),
				eqPermanent(),
				deletedAtIsNull()
			)
			.orderBy(voice.id.desc())
			.limit(request.size() + 1L)
			.fetch();
	}

	private BooleanExpression eqCommunityId(Long communityId) {
		return associate.community.id.eq(communityId);
	}

	private BooleanExpression ltId(Long cursor) {
		return cursor == null ? null : voice.id.lt(cursor);
	}

	private BooleanExpression likeKeyword(String keyword) {
		if (!hasText(keyword))
			return null;
		return voice.name.containsIgnoreCase(keyword.trim());
	}

	private BooleanExpression eqPermanent() {
		return voice.temporary.eq(false);
	}

	private BooleanExpression deletedAtIsNull() {
		return voice.deletedAt.isNull();
	}
}