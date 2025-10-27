package com.memento.server.spring.api.controller.mbti;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.mbti.dto.request.CreateMbtiRequest;
import com.memento.server.api.service.mbti.dto.response.SearchMbtiResponse;
import com.memento.server.domain.mbti.Mbti;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class MbtiControllerTest extends ControllerTestSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/mbti-tests";

	@Test
	@DisplayName("mbti 등록")
	void createTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 2L;

		CreateMbtiRequest request = CreateMbtiRequest.builder()
			.mbti(Mbti.ENFJ)
			.build();

		doNothing().when(mbtiService).create(anyLong(), anyLong(), anyLong(), eq(Mbti.ENFJ));

		//when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L,1L,1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 커뮤니티의 mbti는 등록할 수 없습니다")
	void createWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 2L;

		CreateMbtiRequest request = CreateMbtiRequest.builder()
			.mbti(Mbti.ENFJ)
			.build();

		doNothing().when(mbtiService).create(anyLong(), anyLong(), anyLong(), eq(Mbti.ENFJ));

		//when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
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
	@DisplayName("자기 자신의 mbti는 등록할 수 없습니다")
	void createWithSameAssociateTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 2L;

		CreateMbtiRequest request = CreateMbtiRequest.builder()
			.mbti(Mbti.ENFJ)
			.build();

		doNothing().when(mbtiService).create(anyLong(), anyLong(), anyLong(), eq(Mbti.ENFJ));

		//when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L,2L,1L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(7005))
			.andExpect(jsonPath("$.message").value("권한이 없는 참여자입니다."));
	}

	@Test
	@DisplayName("request의 mbti는 null일 수 없습니다")
	void createWithNullRequestTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 2L;

		CreateMbtiRequest request = CreateMbtiRequest.builder()
			.build();

		doNothing().when(mbtiService).create(anyLong(), anyLong(), anyLong(), eq(Mbti.ENFJ));

		//when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON)
					.with(withJwt(1L,1L,1L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"));
	}

	@Test
	@DisplayName("mbti 조회")
	void searchTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		SearchMbtiResponse response = SearchMbtiResponse.builder()
			.INFP(0L)
			.INFJ(0L)
			.INTP(4L)
			.INTJ(5L)
			.ISFP(0L)
			.ISFJ(0L)
			.ISTP(3L)
			.ISTJ(10L)
			.ENFP(0L)
			.ENFJ(0L)
			.ENTP(2L)
			.ENTJ(1L)
			.ESFP(0L)
			.ESFJ(0L)
			.ESTP(0L)
			.ESTJ(0L)
			.build();

		when(mbtiService.search(anyLong(), anyLong())).thenReturn(response);

		//when & then
		mockMvc.perform(
				get(PATH, communityId, associateId)
					.with(withJwt(1L,1L,1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("다른 그룹의 mbti는 조회할 수 없습니다")
	void searchWithDifferentCommunityTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		SearchMbtiResponse response = SearchMbtiResponse.builder()
			.INFP(0L)
			.INFJ(0L)
			.INTP(4L)
			.INTJ(5L)
			.ISFP(0L)
			.ISFJ(0L)
			.ISTP(3L)
			.ISTJ(10L)
			.ENFP(0L)
			.ENFJ(0L)
			.ENTP(2L)
			.ENTJ(1L)
			.ESFP(0L)
			.ESFJ(0L)
			.ESTP(0L)
			.ESTJ(0L)
			.build();

		when(mbtiService.search(anyLong(), anyLong())).thenReturn(response);

		//when & then
		mockMvc.perform(
				get(PATH, communityId, associateId)
					.with(withJwt(1L,1L,2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}
}
