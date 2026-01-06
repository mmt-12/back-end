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

import com.memento.server.api.controller.member.dto.EmailCheckResponse;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.member.dto.MemberNormalSignUpRequest;
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
		when(memberService.checkDuplicateEmail(any())).thenReturn(EmailCheckResponse.of(true));

		// when & then
		mockMvc.perform(
				get("/api/v1/members/check-email")
					.param("email", email))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isAvailable").value(true));
	}

	@Test
	@DisplayName("이메일 중복 체크 API - 중복된 이메일")
	void checkDuplicateEmail_duplicate() throws Exception {
		// given
		String email = "existing@example.com";
		when(memberService.checkDuplicateEmail(any())).thenReturn(EmailCheckResponse.of(false));

		// when & then
		mockMvc.perform(
				get("/api/v1/members/check-email")
					.param("email", email))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isAvailable").value(false));
	}

	@Test
	@DisplayName("이메일 중복 체크 API - 잘못된 이메일 형식")
	void checkDuplicateEmail_invalidFormat() throws Exception {
		// given
		String invalidEmail = "invalid-email";

		// when & then
		mockMvc.perform(
				get("/api/v1/members/check-email")
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
					.param("email", ""))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("일반 회원가입 API")
	void normalSignUp_success() throws Exception {
		// given
		MemberNormalSignUpRequest request = MemberNormalSignUpRequest.builder()
			.name("홍길동")
			.email("hong@test.com")
			.password("password123")
			.birthday(LocalDate.of(1990, 1, 1))
			.secret("오렌지")
			.fcmToken("fcm-token-123")
			.build();

		doNothing().when(memberService).normalSignUp(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/members/signup/normal")
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("일반 회원가입 API - secret 틀림")
	void normalSignUp_invalidSecret() throws Exception {
		// given
		MemberNormalSignUpRequest request = MemberNormalSignUpRequest.builder()
			.name("홍길동")
			.email("hong@test.com")
			.password("password123")
			.birthday(LocalDate.of(1990, 1, 1))
			.secret("잘못된비밀")
			.fcmToken("fcm-token-123")
			.build();

		doThrow(new MementoException(ErrorCodes.MEMBER_SECRET_INVALID))
			.when(memberService).normalSignUp(any());

		// when & then
		mockMvc.perform(
				post("/api/v1/members/signup/normal")
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(4011));
	}

	@Test
	@DisplayName("회원가입 결과 페이지 (GET) - HTML 반환")
	void signUpPage_returnsHtml() throws Exception {
		// given
		when(templateEngine.process(any(String.class), any())).thenReturn("<html></html>");

		// when & then
		mockMvc.perform(
				get("/api/v1/members/signup/page")
					.param("memberId", "1")
					.param("action", "accept")
					.param("token", "test-token-123"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("회원가입 결과 API (POST) - 승인")
	void signUpResult_accept() throws Exception {
		// given
		doNothing().when(memberService).signUpResult(any());
		String requestBody = """
			{"memberId": 1, "token": "test-token-123", "action": "accept"}
			""";

		// when & then
		mockMvc.perform(
				post("/api/v1/members/signup/result")
					.content(requestBody)
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("회원가입 결과 API (POST) - 거절")
	void signUpResult_reject() throws Exception {
		// given
		doNothing().when(memberService).signUpResult(any());
		String requestBody = """
			{"memberId": 1, "token": "test-token-123", "action": "reject"}
			""";

		// when & then
		mockMvc.perform(
				post("/api/v1/members/signup/result")
					.content(requestBody)
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("회원가입 결과 API (POST) - 토큰 검증 실패")
	void signUpResult_invalidToken() throws Exception {
		// given
		doThrow(new MementoException(ErrorCodes.SIGNUP_TOKEN_INVALID))
			.when(memberService).signUpResult(any());
		String requestBody = """
			{"memberId": 1, "token": "invalid-token", "action": "accept"}
			""";

		// when & then
		mockMvc.perform(
				post("/api/v1/members/signup/result")
					.content(requestBody)
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(18000));
	}

}
