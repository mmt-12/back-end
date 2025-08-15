package com.memento.server.spring.api.controller.achievement;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.achievement.dto.SearchAchievementResponse;
import com.memento.server.domain.achievement.AchievementType;
import com.memento.server.spring.api.controller.ControllerTestSupport;

public class AchievementControllerTest extends ControllerTestSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/achievements";

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
				get(PATH, communityId, associateId)
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
				get(PATH, communityId, associateId)
					.with(withJwt(1L, 1L, 2L)))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(5006))
			.andExpect(jsonPath("$.message").value("다른 그룹의 요청입니다."));
	}

}
