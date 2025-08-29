package com.memento.server.domain.notification;

import static com.memento.server.domain.notification.QNotification.notification;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;
import com.memento.server.api.service.notification.dto.response.QNotificationResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<NotificationResponse> findNotificationsByAssociateWithCursor(NotificationListQueryRequest request) {
		return queryFactory
			.select(new QNotificationResponse(
				notification.id,
				notification.title,
				notification.content,
				notification.isRead,
				notification.type.stringValue(),
				notification.actorId,
				notification.memoryId,
				notification.postId,
				notification.createdAt
			))
			.from(notification)
			.where(
				eqReceiverId(request.associateId()),
				ltId(request.cursor()),
				deletedAtIsNull()
			)
			.orderBy(notification.id.desc())
			.limit(request.size() + 1L)
			.fetch();
	}

	@Override
	public int countUnreadNotificationsByAssociate(Long associateId) {
		Long count = queryFactory
			.select(notification.count())
			.from(notification)
			.where(
				eqReceiverId(associateId),
				eqIsRead(false),
				deletedAtIsNull()
			)
			.fetchOne();
		
		return count != null ? count.intValue() : 0;
	}

	private BooleanExpression eqReceiverId(Long associateId) {
		return notification.receiver.id.eq(associateId);
	}

	private BooleanExpression ltId(Long cursor) {
		return cursor == null ? null : notification.id.lt(cursor);
	}

	private BooleanExpression eqIsRead(Boolean isRead) {
		return notification.isRead.eq(isRead);
	}

	private BooleanExpression deletedAtIsNull() {
		return notification.deletedAt.isNull();
	}
}