package com.memento.server.domain.voice;

import static com.memento.server.domain.voice.QVoice.voice;
import static com.memento.server.domain.community.QAssociate.associate;
import static com.memento.server.domain.member.QMember.member;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VoiceRepositoryImpl implements VoiceRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Voice> findVoicesWithPagination(VoiceListQueryRequest request) {
		BooleanBuilder whereClause = buildWhereClause(request);
		
		return queryFactory
			.selectFrom(voice)
			.join(voice.associate, associate).fetchJoin()
			.join(associate.member, member).fetchJoin()
			.where(whereClause)
			.orderBy(voice.id.desc())
			.limit(request.size() + 1) // +1로 hasNext 판단
			.fetch();
	}

	@Override
	public boolean hasNextVoice(VoiceListQueryRequest request, Long lastVoiceId) {
		BooleanBuilder whereClause = buildWhereClause(request);
		
		if (lastVoiceId != null) {
			whereClause.and(voice.id.lt(lastVoiceId));
		}
		
		return queryFactory
			.selectFrom(voice)
			.where(whereClause)
			.limit(1)
			.fetchFirst() != null;
	}
	
	private BooleanBuilder buildWhereClause(VoiceListQueryRequest request) {
		BooleanBuilder builder = new BooleanBuilder();
		
		// 커뮤니티 ID 조건
		if (request.groupId() != null) {
			builder.and(associate.community.id.eq(request.groupId()));
		}
		
		// permanent 보이스만 조회 (temporary=false)
		builder.and(voice.temporary.eq(false));
		
		// 커서 기반 페이지네이션
		if (request.cursor() != null) {
			builder.and(voice.id.lt(request.cursor()));
		}
		
		// 키워드 검색 (보이스 이름)
		if (request.keyword() != null && !request.keyword().isBlank()) {
			builder.and(voice.name.containsIgnoreCase(request.keyword()));
		}
		
		return builder;
	}
}