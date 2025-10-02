package com.memento.server.spring.api.controller.fcm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.fcm.request.SaveFCMTokenRequest;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class FCMControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("FCM 토큰을 저장한다.")
	void saveFCMToken() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 1L;
		long memberId = 1L;
		SaveFCMTokenRequest request = SaveFCMTokenRequest.builder()
			.token("valid_fcm_token_1234567890")
			.build();

		doNothing().when(fcmService).saveFCMToken(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/fcm")
					.with(withJwt(memberId, associateId, communityId))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated());

		verify(fcmService).saveFCMToken(any());
	}

	@Test
	@DisplayName("FCM 토큰 저장 시 token은 필수값이다.")
	void saveFCMTokenWithoutToken() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 1L;
		long memberId = 1L;
		SaveFCMTokenRequest request = SaveFCMTokenRequest.builder().build();

		doNothing().when(fcmService).saveFCMToken(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/fcm")
					.with(withJwt(memberId, associateId, communityId))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("token"))
			.andExpect(jsonPath("$.errors[0].message").value("token 값은 필수 입니다."));

		verify(fcmService, never()).saveFCMToken(any());
	}

	@Test
	@DisplayName("FCM 토큰 저장 시 token은 공백일 수 없다.")
	void saveFCMTokenWithBlankToken() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 1L;
		long memberId = 1L;
		SaveFCMTokenRequest request = SaveFCMTokenRequest.builder()
			.token("   ")
			.build();

		doNothing().when(fcmService).saveFCMToken(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/fcm")
					.with(withJwt(memberId, associateId, communityId))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("token"))
			.andExpect(jsonPath("$.errors[0].message").value("token 값은 필수 입니다."));

		verify(fcmService, never()).saveFCMToken(any());
	}

	@Test
	@DisplayName("FCM 토큰 저장 시 token 길이는 512자를 초과할 수 없다.")
	void saveFCMTokenWithTooLongToken() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 1L;
		long memberId = 1L;
		String tooLongToken = "a".repeat(513);
		SaveFCMTokenRequest request = SaveFCMTokenRequest.builder()
			.token(tooLongToken)
			.build();

		doNothing().when(fcmService).saveFCMToken(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/fcm")
					.with(withJwt(memberId, associateId, communityId))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"));

		verify(fcmService, never()).saveFCMToken(any());
	}

	@Test
	@DisplayName("이미 저장된 FCM 토큰을 저장하려고 하면 예외가 발생한다.")
	void saveDuplicateFCMToken() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 1L;
		long memberId = 1L;
		SaveFCMTokenRequest request = SaveFCMTokenRequest.builder()
			.token("duplicate_token")
			.build();

		doThrow(new MementoException(ErrorCodes.FCMTOKEN_DUPLICATE))
			.when(fcmService).saveFCMToken(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/fcm")
					.with(withJwt(memberId, associateId, communityId))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(16000))
			.andExpect(jsonPath("$.message").value("이미 저장된 fcm token입니다."));

		verify(fcmService).saveFCMToken(any());
	}

	@Test
	@DisplayName("존재하지 않는 Associate로 FCM 토큰 저장 시 예외가 발생한다.")
	void saveFCMTokenWithNonExistentAssociate() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 999L;
		long memberId = 1L;
		SaveFCMTokenRequest request = SaveFCMTokenRequest.builder()
			.token("valid_token")
			.build();

		doThrow(new MementoException(ErrorCodes.ASSOCIATE_NOT_FOUND))
			.when(fcmService).saveFCMToken(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/fcm")
					.with(withJwt(memberId, associateId, communityId))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(7008))
			.andExpect(jsonPath("$.message").value("그룹 참여자를 찾을 수 없습니다."));

		verify(fcmService).saveFCMToken(any());
	}
}
