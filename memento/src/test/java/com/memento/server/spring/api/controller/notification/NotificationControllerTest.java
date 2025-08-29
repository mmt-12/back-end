package com.memento.server.spring.api.controller.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationListResponse;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;
import com.memento.server.api.service.notification.dto.response.NotificationUnreadResponse;
import com.memento.server.common.dto.response.PageInfo;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class NotificationControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("알림 목록을 조회한다.")
	void getNotifications() throws Exception {
		long communityId = 1L;
		long associateId = 1L;
		long memberId = 1L;
		long cursor = 1L;
		int size = 10;
		Long nextCursor = cursor + size;
		boolean hasNext = true;

		NotificationResponse notificationResponse = NotificationResponse.builder()
			.id(1L)
			.title("title")
			.content("content")
			.isRead(true)
			.type("MEMORY")
			.actorId(1L)
			.memoryId(1L)
			.postId(1L)
			.createdAt(LocalDateTime.now())
			.build();

		NotificationListResponse response = NotificationListResponse.of(List.of(notificationResponse),
			PageInfo.of(hasNext, nextCursor));

		given(notificationService.getNotifications(any(NotificationListQueryRequest.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(
				get("/api/v1/notifications")
					.param("cursor", String.valueOf(cursor))
					.param("size", String.valueOf(size))
					.with(withJwt(memberId, associateId, communityId)))
			.andExpect(status().isOk());

		verify(notificationService).getNotifications(any(NotificationListQueryRequest.class));
	}

	@Test
	@DisplayName("안읽은 알림이 있는지 여부와 개수를 조회한다.")
	void getUnread() throws Exception {
	    // given
		long communityId = 1L;
		long associateId = 1L;
		long memberId = 1L;

		NotificationUnreadResponse response = NotificationUnreadResponse.of(true, 5);

		given(notificationService.getUnread(associateId)).willReturn(response);
	    // when && then
		mockMvc.perform(
				get("/api/v1/notifications/unread")
					.with(withJwt(memberId, associateId, communityId)))
			.andExpect(status().isOk());

		verify(notificationService).getUnread(any());
	}
}
