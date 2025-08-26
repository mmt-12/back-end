package com.memento.server.docs.notification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.memento.server.api.controller.notification.NotificationController;
import com.memento.server.api.service.notification.NotificationService;
import com.memento.server.api.service.notification.dto.request.NotificationListQueryRequest;
import com.memento.server.api.service.notification.dto.response.NotificationListResponse;
import com.memento.server.api.service.notification.dto.response.NotificationResponse;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.domain.notification.NotificationType;
import com.memento.server.notification.NotificationFixtures;

public class NotificationControllerDocsTest extends RestDocsSupport {

	private final NotificationService notificationService = Mockito.mock(NotificationService.class);

	@Override
	protected Object initController() {
		return new NotificationController(notificationService);
	}

	@Test
	@DisplayName("알림 목록을 조회한다.")
	void getNotifications() throws Exception {
		setAuthentication(1L, 1L, 1L);

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
					.param("size", String.valueOf(size)))
			.andExpect(status().isOk())
			.andDo(document("notification-get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("cursor").description("현재 페이지의 마지막 알림 ID (첫 페이지는 null)").optional(),
					parameterWithName("size").description("요청할 알림 수 (기본값: 10)").optional()
				),
				responseFields(
					fieldWithPath("notifications[]").description("알림 목록"),
					fieldWithPath("notifications[].id").description("알림 아이디"),
					fieldWithPath("notifications[].title").description("알림 제목"),
					fieldWithPath("notifications[].content").description("알림 내용"),
					fieldWithPath("notifications[].isRead").description("읽음 여부"),
					fieldWithPath("notifications[].type").description(
						"알림 타입 (ACHIEVE, MEMORY, REACTION, POST, BIRTHDAY, ASSOCIATE, GUESTBOOK, MBTI, NEWIMAGE)"),
					fieldWithPath("notifications[].actorId").description("알림을 발생시킨 사용자 ID (시스템 알림의 경우 null)")
						.optional(),
					fieldWithPath("notifications[].memoryId").description("관련된 기억 ID (추억 관련 알림의 경우)").optional(),
					fieldWithPath("notifications[].postId").description("관련된 게시글 ID (게시글 관련 알림의 경우)").optional(),
					fieldWithPath("notifications[].createdAt").description("알림 생성 시간"),
					fieldWithPath("cursor").description("현재 커서 위치 (마지막으로 조회한 알림 ID)"),
					fieldWithPath("size").description("요청한 알림 수"),
					fieldWithPath("nextCursor").description("다음 페이지 커서 (더 불러올 알림이 있을 경우)").optional(),
					fieldWithPath("hasNext").description("다음 페이지 존재 여부")
				)
			));

		verify(notificationService).getNotifications(any(NotificationListQueryRequest.class));
	}
}
