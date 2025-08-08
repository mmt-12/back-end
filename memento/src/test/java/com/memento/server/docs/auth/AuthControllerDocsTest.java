package com.memento.server.docs.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.auth.AuthController;
import com.memento.server.api.controller.auth.AuthGuestResponse;
import com.memento.server.api.controller.auth.AuthMemberResponse;
import com.memento.server.api.controller.auth.AuthResponse;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.api.service.auth.AuthService;
import com.memento.server.api.service.auth.jwt.JwtToken;

public class AuthControllerDocsTest extends RestDocsSupport {

	private final AuthService authService = mock(AuthService.class);

	@Override
	protected Object initController() {
		return new AuthController(authService);
	}

	@Test
	@DisplayName("카카오 로그인 인증 URL 리디렉션")
	void signIn() throws Exception {
		// given
		when(authService.getAuthUrl()).thenReturn("https://kauth.kakao.com/oauth/authorize?client_id=...");

		// when & then
		mockMvc.perform(post("/api/v1/sign-in"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("https://kauth.kakao.com/oauth/authorize?client_id=..."))
			.andDo(document("auth-sign-in",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint())
			));
	}

	@Test
	@DisplayName("카카오 인가 코드 콜백 처리 | 미가입자")
	void handleRedirectWithGuest() throws Exception {
		// given
		JwtToken jwtToken = JwtToken.builder()
			.grantType("Bearer")
			.accessToken("access-token-123")
			.accessTokenExpiresAt(new Date())
			.build();
		AuthResponse response = new AuthGuestResponse(123L, "email@naver.com", jwtToken);

		when(authService.handleAuthorizationCallback("code123")).thenReturn(response);

		// when & then
		mockMvc.perform(get("/redirect")
				.param("code", "code123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.kakaoId").value(123L))
			.andExpect(jsonPath("$.email").value("email@naver.com"))
			.andExpect(jsonPath("$.token.grantType").value("Bearer"))
			.andExpect(jsonPath("$.token.accessToken").value("access-token-123"))
			.andDo(document("auth-redirect-test-guest",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("kakaoId").description("카카오 아이디"),
					fieldWithPath("email").description("카카오 이메일"),
					subsectionWithPath("token").description("JWT 토큰 정보"),
					fieldWithPath("token.grantType").description("토큰 타입"),
					fieldWithPath("token.accessToken").description("액세스 토큰"),
					fieldWithPath("token.accessTokenExpiresAt").description("액세스 토큰 만료 시각"),
					fieldWithPath("token.refreshToken").description("리프레시 토큰"),
					fieldWithPath("token.refreshTokenExpiresAt").description("리프레시 토큰 만료 시각")
				)
			));
	}

	@Test
	@DisplayName("카카오 인가 코드 콜백 처리 | 가입자")
	void handleRedirectWithMember() throws Exception {
		// given
		JwtToken jwtToken = JwtToken.builder()
			.grantType("Bearer")
			.accessToken("access-token-123")
			.accessTokenExpiresAt(new Date())
			.refreshToken("refresh-token-456")
			.refreshTokenExpiresAt(new Date())
			.build();
		AuthResponse response = new AuthMemberResponse(123L, "name", jwtToken);

		when(authService.handleAuthorizationCallback("code123")).thenReturn(response);

		// when & then
		mockMvc.perform(get("/redirect")
				.param("code", "code123"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberId").value(123L))
			.andExpect(jsonPath("$.name").value("name"))
			.andExpect(jsonPath("$.token.grantType").value("Bearer"))
			.andExpect(jsonPath("$.token.accessToken").value("access-token-123"))
			.andExpect(jsonPath("$.token.refreshToken").value("refresh-token-456"))
			.andDo(document("auth-redirect-test-member",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("memberId").description("사용자 ID"),
					fieldWithPath("name").description("사용자 이름"),
					subsectionWithPath("token").description("JWT 토큰 정보"),
					fieldWithPath("token.grantType").description("토큰 타입"),
					fieldWithPath("token.accessToken").description("액세스 토큰"),
					fieldWithPath("token.accessTokenExpiresAt").description("액세스 토큰 만료 시각"),
					fieldWithPath("token.refreshToken").description("리프레시 토큰"),
					fieldWithPath("token.refreshTokenExpiresAt").description("리프레시 토큰 만료 시각")
				)
			));
	}
}
