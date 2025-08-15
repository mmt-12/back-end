package com.memento.server.spring.api.controller.community;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.community.dto.SearchAssociateResponse;
import com.memento.server.api.controller.community.dto.UpdateAssociateRequest;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class AssociateControllerTest extends ControllerTestSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates";

	@Test
	@DisplayName("associate 조회")
	void searchTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		SearchAssociateResponse response = SearchAssociateResponse.builder()
			.nickname("example")
			.achievement(SearchAssociateResponse.Achievement.builder()
				.id(1L)
				.name("example")
				.build())
			.imageUrl("www.example.com/example")
			.introduction("example introduction")
			.birthday(LocalDate.of(1999, 1, 1))
			.build();

		given(associateService.search(anyLong(), anyLong()))
			.willReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH + "/{associateId}", communityId, associateId)
					.with(withJwt(1L,1L,1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("같은 그룹의 참여자만 조회 가능합니다.")
	void searchWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		SearchAssociateResponse response = SearchAssociateResponse.builder()
			.nickname("example")
			.achievement(SearchAssociateResponse.Achievement.builder()
				.id(1L)
				.name("example")
				.build())
			.imageUrl("www.example.com/example")
			.introduction("example introduction")
			.birthday(LocalDate.of(1999, 1, 1))
			.build();

		given(associateService.search(anyLong(), anyLong()))
			.willReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH + "/{associateId}", communityId, associateId)
					.with(withJwt(1L,1L,2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("associate 수정 API")
	void updateTest() throws Exception {
		// given
		Long communityId = 1L;

		UpdateAssociateRequest request = UpdateAssociateRequest.builder()
			.profileImageUrl("https://...")
			.nickname("nickname")
			.achievement(1L)
			.introduction("introduction")
			.build();
		doNothing().when(associateService).update(anyLong(), anyLong(), anyString(), anyString(), anyLong(), anyString());

		// when & then
		mockMvc.perform(
				put(PATH, communityId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L,1L,1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("현재 접속한 그룹과 같은 경우에만 프로필만 수정 가능")
	void updateWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;

		UpdateAssociateRequest request = UpdateAssociateRequest.builder()
			.profileImageUrl("https://...")
			.nickname("nickname")
			.achievement(1L)
			.introduction("introduction")
			.build();
		doNothing().when(associateService).update(anyLong(), anyLong(), anyString(), anyString(), anyLong(), anyString());

		// when & then
		mockMvc.perform(
				put(PATH, communityId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L,1L,2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("Request의 각 필드는 최대 크기가 정해져있습니다.")
	void updateWithRequestFiledTest() throws Exception {
		// given
		Long communityId = 1L;

		UpdateAssociateRequest request = UpdateAssociateRequest.builder()
			.profileImageUrl("https://...")
			.nickname("123456789012345678901234567890123456789012345678901234567890")
			.achievement(1L)
			.introduction("introduction")
			.build();
		doNothing().when(associateService).update(anyLong(), anyLong(), anyString(), anyString(), anyLong(), anyString());

		// when & then
		mockMvc.perform(
				put(PATH, communityId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L,1L,1L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"));

	}
}
