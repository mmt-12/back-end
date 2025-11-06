package com.memento.server.spring.api.controller.achievement;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.achievement.dto.request.CreateAchievementRequest;
import com.memento.server.api.service.achievement.dto.response.SearchAchievementResponse;
import com.memento.server.domain.achievement.AchievementType;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class AchievementControllerTest extends ControllerTestSupport {

	public static final String PATH = "/api/v1/communities/{communityId}";

	@Test
	@DisplayName("업적을 조회한다.")
	void searchTest() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 1L;

		SearchAchievementResponse response = SearchAchievementResponse.builder()
			.achievements(List.of(
				SearchAchievementResponse.Achievement.builder()
					.id(1L)
					.name("업적1")
					.criteria("조건1")
					.isObtained(true)
					.type(AchievementType.OPEN)
					.build()
			))
			.build();

		given(achievementService.search(anyLong(), anyLong())).willReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH + "/associates/{associateId}/achievements", communityId, associateId)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("같은 그룹의 업적만 조회가 가능하다.")
	void searchWithDifferentCommunityIdTest() throws Exception {
		// given
		long communityId = 1L;
		long associateId = 1L;

		SearchAchievementResponse response = SearchAchievementResponse.builder()
			.achievements(List.of(
				SearchAchievementResponse.Achievement.builder()
					.id(1L)
					.name("업적1")
					.criteria("조건1")
					.isObtained(true)
					.type(AchievementType.OPEN)
					.build()
			))
			.build();

		given(achievementService.search(anyLong(), anyLong())).willReturn(response);

		// when & then
		mockMvc.perform(
				get(PATH + "/associates/{associateId}/achievements", communityId, associateId)
					.with(withJwt(1L, 1L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

	@Test
	@DisplayName("업적 생성 API")
	void createTest() throws Exception{
		// given
		long communityId = 1L;
		CreateAchievementRequest request = CreateAchievementRequest.builder()
			.content("HOME")
			.build();

		doNothing().when(achievementService).create(anyLong(), eq("HOME"));
		// when & then
		mockMvc.perform(
				post(PATH + "/achievements", communityId)
					.with(withJwt(1L, 1L, 1L))
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("전용 업적 API")
	void exclusiveTest() throws Exception{
		// given
		long communityId = 1L;

		doNothing().when(achievementService).exclusive(anyLong(), anyLong());
		// when & then
		mockMvc.perform(
				post(PATH + "/achievements/exclusive", communityId)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("출석 업적 API")
	void attendanceTest() throws Exception{
		// given
		long communityId = 1L;

		doNothing().when(achievementService).attendance(anyLong(), anyLong());
		// when & then
		mockMvc.perform(
				post(PATH + "/achievements/attendance", communityId)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isOk());
	}
}
