package com.memento.server.spring.api.controller.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.memory.dto.ReadMemoryResponse;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class ControllerAuthTest extends ControllerTestSupport {

	@Test
	@DisplayName("정상 토큰 테스트")
	void readMemory() throws Exception {
		// given
		ReadMemoryResponse readMemoryResponse = ReadMemoryResponse.builder().build();

		when(memoryService.read(any())).thenReturn(readMemoryResponse);

		// when && then
		mockMvc.perform(
				get("/api/v1/communities/{communityId}/memories/{memoryId}", 1L, 1L)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("토큰 없을 경우 401 응답이 반환되어야 한다.")
	void readMemoryException_withoutToken() throws Exception {
		// given
		ReadMemoryResponse readMemoryResponse = ReadMemoryResponse.builder().build();

		when(memoryService.read(any())).thenReturn(readMemoryResponse);

		// when && then
		mockMvc.perform(
				get("/api/v1/communities/{communityId}/memories/{memoryId}", 1L, 1L))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("토큰이 올바르지 않을 경우 401 응답이 반환되어야 한다.")
	void readMemoryException_withInvalidToken() throws Exception {
		// when && then
		mockMvc.perform(
				get("/api/v1/communities/{communityId}/memories/{memoryId}", 1L, 1L)
					.with(withInvalidJwt()))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("그룹 아이디가 없을 경우 401 응답이 반환되어야 한다.")
	void readMemoryException_withoutCommunityId() throws Exception {
		// when && then
		mockMvc.perform(
				get("/api/v1/communities/{communityId}/memories/{memoryId}", 1L, 1L)
					.with(withJwt(1L, 1L, null)))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
}
