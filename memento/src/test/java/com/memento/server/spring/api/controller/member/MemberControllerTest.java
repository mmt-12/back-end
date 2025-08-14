package com.memento.server.spring.api.controller.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.memento.server.api.controller.member.MemberController;
import com.memento.server.api.controller.member.dto.MemberSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.api.service.member.MemberService;
import com.memento.server.spring.api.controller.ControllerTestSupport;

@WebMvcTest({
	MemberController.class
})
public class MemberControllerTest extends ControllerTestSupport {

	@MockitoBean
	private MemberService memberService;

	@MockitoBean
	private AssociateService associateService;

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

		when(memberService.signUp(any(), any(), any(), any())).thenReturn(memberSignUpResponse);

		// when && then
		mockMvc.perform(
				post("/api/v1/members")
					.with(withJwt(1L, null, null))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(request.name()));
	}
}
