package com.memento.server.docs.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.memento.server.api.controller.member.MemberController;
import com.memento.server.api.controller.member.MemberSignUpRequest;
import com.memento.server.api.controller.member.MemberSignUpResponse;
import com.memento.server.api.controller.member.MemberUpdateRequest;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.member.MemberService;

public class MemberControllerTestDocsTest extends RestDocsSupport {

	private final MemberService memberService = mock(MemberService.class);

	@Override
	protected Object initController() {
		return new MemberController(memberService);
	}

	@Test
	@DisplayName("회원가입")
	void signUp() throws Exception {
		// given
		setAuthentication(1L, null, null);
		MemberSignUpRequest request = new MemberSignUpRequest("name", "email@naver.com",
			LocalDate.of(2025, 8, 4));
		JwtToken jwtToken = JwtToken.builder()
			.grantType("Bearer")
			.accessToken("access-token-123")
			.accessTokenExpiresAt(new Date())
			.refreshToken("refresh-token-456")
			.refreshTokenExpiresAt(new Date())
			.build();
		MemberSignUpResponse response = new MemberSignUpResponse(1L, "name", jwtToken);
		when(memberService.signUp(any(), any(), any(), any())).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memberId").value(1L))
			.andExpect(jsonPath("$.name").value("name"))
			.andExpect(jsonPath("$.token.grantType").value("Bearer"))
			.andExpect(jsonPath("$.token.accessToken").value("access-token-123"))
			.andExpect(jsonPath("$.token.refreshToken").value("refresh-token-456"))
			.andDo(document("member-signup-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("name").type(STRING).description("이름"),
					fieldWithPath("email").type(STRING).description("이메일"),
					fieldWithPath("birthday").type(ARRAY).description("생일 (\"YYYY-MM-DD\"")
				),
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

	@Test
	@DisplayName("회원 정보 수정")
	void update() throws Exception {
		// given
		setAuthentication(1L, null, null);
		MemberUpdateRequest request = MemberUpdateRequest.builder()
			.name("name2")
			.email("email2@naver.com")
			.build();
		doNothing().when(memberService).update(any(), any(), any());

		// when & then
		mockMvc.perform(put("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(document("member-update-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("name").type(STRING).description("이름"),
					fieldWithPath("email").type(STRING).description("이메일")
				)
			));
	}
}
