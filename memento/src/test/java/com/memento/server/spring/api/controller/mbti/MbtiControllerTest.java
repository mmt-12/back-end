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

import com.memento.server.api.controller.mbti.dto.CreateMbtiRequest;
import com.memento.server.api.controller.mbti.dto.SearchMbtiResponse;
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
			.andExpect(jsonPath("$.code").value(7005))
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
			.andExpect(jsonPath("$.code").value(7006))
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
			.INFP(0)
			.INFJ(0)
			.INTP(4)
			.INTJ(5)
			.ISFP(0)
			.ISFJ(0)
			.ISTP(3)
			.ISTJ(10)
			.ENFP(0)
			.ENFJ(0)
			.ENTP(2)
			.ENTJ(1)
			.ESFP(0)
			.ESFJ(0)
			.ESTP(0)
			.ESTJ(0)
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
			.INFP(0)
			.INFJ(0)
			.INTP(4)
			.INTJ(5)
			.ISFP(0)
			.ISFJ(0)
			.ISTP(3)
			.ISTJ(10)
			.ENFP(0)
			.ENFJ(0)
			.ENTP(2)
			.ENTJ(1)
			.ESFP(0)
			.ESFJ(0)
			.ESTP(0)
			.ESTJ(0)
			.build();

		when(mbtiService.search(anyLong(), anyLong())).thenReturn(response);

		//when & then
		mockMvc.perform(
				get(PATH, communityId, associateId)
					.with(withJwt(1L,1L,2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(7005))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}
}
