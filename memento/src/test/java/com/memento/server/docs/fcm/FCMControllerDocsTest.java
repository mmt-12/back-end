package com.memento.server.docs.fcm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.memento.server.api.controller.fcm.FCMController;
import com.memento.server.api.controller.fcm.request.SaveFCMTokenRequest;
import com.memento.server.api.service.fcm.FCMService;
import com.memento.server.docs.RestDocsSupport;

public class FCMControllerDocsTest extends RestDocsSupport {

	private final FCMService fcmService = Mockito.mock(FCMService.class);

	@Override
	protected Object initController() {
		return new FCMController(fcmService);
	}

	@Test
	@DisplayName("FCM 토큰을 저장한다.")
	void saveFCMToken() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 1L;
		long memberId = 1L;
		setAuthentication(memberId, associateId, communityId);

		SaveFCMTokenRequest request = SaveFCMTokenRequest.builder()
			.token("fcm_token_example_1234567890abcdefghijklmnopqrstuvwxyz")
			.build();

		doNothing().when(fcmService).saveFCMToken(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/fcm")
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("fcm-token-save",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("token").type(STRING).description("FCM 토큰 (최대 4096자)")
				)
			));

		verify(fcmService).saveFCMToken(any());
	}
}