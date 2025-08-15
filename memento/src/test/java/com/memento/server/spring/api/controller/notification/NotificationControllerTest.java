package com.memento.server.spring.api.controller.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationListResponse;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;
import com.memento.server.domain.notification.NotificationType;
import com.memento.server.spring.api.controller.ControllerTestSupport;
import com.memento.server.notification.NotificationFixtures;

public class NotificationControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("알림 목록을 조회한다.")
	void getNotifications() throws Exception {
		long cursor = 1L;
		int size = 10;
		Long nextCursor = cursor + size;
		boolean hasNext = true;

		NotificationResponse notificationResponse = NotificationResponse.from(NotificationFixtures.notificationWithType(
			NotificationType.ACHIEVE));
		NotificationListResponse response = NotificationListResponse.of(List.of(notificationResponse), cursor, size,
			nextCursor, hasNext);

		given(notificationService.getNotifications(any(NotificationListQueryRequest.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(
				get("/api/v1/notifications")
					.param("cursor", String.valueOf(cursor))
					.param("size", String.valueOf(size))
					.with(withJwt(1L, 1L, 1L)))
			.andExpect(status().isOk());

		verify(notificationService).getNotifications(any(NotificationListQueryRequest.class));
	}
}
