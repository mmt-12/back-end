package com.memento.server.spring.api.controller.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.member.dto.MemberSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class MemberControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("회원가입 API 파라미터 매핑 테스트")
	void signup() throws Exception {
		// given
		MemberSignUpRequest request = MemberSignUpRequest.builder()
			.name("name")
			.email("email@naver.com")
			.birthday(LocalDate.of(2025, 8, 4))
			.build();
		JwtToken jwtToken = JwtToken.builder()
			.grantType("Bearer")
			.accessToken("access-token-123")
			.accessTokenExpiresAt(new Date())
			.refreshToken("refresh-token-456")
			.refreshTokenExpiresAt(new Date())
			.build();
		MemberSignUpResponse memberSignUpResponse = new MemberSignUpResponse(1L, "name", jwtToken);

		when(memberService.signUp(any(), any())).thenReturn(memberSignUpResponse);

		// when && then
		mockMvc.perform(
				post("/api/v1/members/signup/kakao")
					.with(withJwt(1L, null, null))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(request.name()));
	}

	@Test
	@DisplayName("이메일 중복 체크 API - 사용 가능한 이메일")
	void checkDuplicateEmail_success() throws Exception {
		// given
		String email = "test@example.com";
		doNothing().when(memberService).checkDuplicateEmail(any());

		// when & then
		mockMvc.perform(
				get("/api/v1/members/check-email")
						.with(withJwt(1L, null, null))
					.param("email", email))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("이메일 중복 체크 API - 중복된 이메일")
	void checkDuplicateEmail_duplicate() throws Exception {
		// given
		String email = "existing@example.com";
		doThrow(new MementoException(ErrorCodes.MEMBER_EMAIL_DUPLICATE))
			.when(memberService).checkDuplicateEmail(any());

		// when & then
		mockMvc.perform(
				get("/api/v1/members/check-email")
						.with(withJwt(1L, null, null))
					.param("email", email))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(4012))
			.andExpect(jsonPath("$.message").value("중복된 email입니다."));
	}

	@Test
	@DisplayName("이메일 중복 체크 API - 잘못된 이메일 형식")
	void checkDuplicateEmail_invalidFormat() throws Exception {
		// given
		String invalidEmail = "invalid-email";

		// when & then
		mockMvc.perform(
				get("/api/v1/members/check-email")
						.with(withJwt(1L, null, null))
					.param("email", invalidEmail))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("이메일 중복 체크 API - 이메일 미입력")
	void checkDuplicateEmail_blank() throws Exception {
		// when & then
		mockMvc.perform(
				get("/api/v1/members/check-email")
						.with(withJwt(1L, null, null))
					.param("email", ""))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
