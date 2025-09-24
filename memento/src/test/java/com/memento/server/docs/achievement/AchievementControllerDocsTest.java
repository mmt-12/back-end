package com.memento.server.docs.achievement;

import static java.sql.JDBCType.ARRAY;
import static java.sql.JDBCType.BOOLEAN;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.achievement.AchievementController;
import com.memento.server.api.controller.achievement.dto.CreateAchievementRequest;
import com.memento.server.api.controller.achievement.dto.SearchAchievementResponse;
import com.memento.server.api.service.achievement.AchievementService;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.domain.achievement.AchievementType;

public class AchievementControllerDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}";

	private final AchievementService achievementService = mock(AchievementService.class);

	@Override
	protected Object initController() {
		return new AchievementController(achievementService);
	}

	@Test
	@DisplayName("업적 조회 API")
	void searchTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		long communityId = 1L;
		long associateId = 1L;

		given(achievementService.search(anyLong(), anyLong()))
			.willReturn(
				SearchAchievementResponse.builder()
					.achievements(List.of(
						SearchAchievementResponse.Achievement.builder()
							.id(1L)
							.name("업적1")
							.criteria("조건1")
							.isObtained(true)
							.type(AchievementType.OPEN)
							.build()
					))
					.build()
			);

		// when & then
		mockMvc.perform(
				get(PATH + "/associates/{associateId}/achievements", communityId, associateId))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("achievement-read-test",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("achievements").type(ARRAY).description("업적 목록"),
					fieldWithPath("achievements[].id").type(NUMBER).description("업적 ID"),
					fieldWithPath("achievements[].name").type(STRING).description("업적 이름"),
					fieldWithPath("achievements[].criteria").type(STRING).description("업적 조건"),
					fieldWithPath("achievements[].obtained").type(BOOLEAN).description("업적 획득 여부"),
					fieldWithPath("achievements[].type").type(STRING).description("업적 타입")
				)
			));
	}

	@Test
	@DisplayName("업적 생성 API")
	void createTest() throws Exception{
		// given
		setAuthentication(1L, 1L, 1L);

		long communityId = 1L;
		CreateAchievementRequest request = CreateAchievementRequest.builder()
			.content("HOME")
			.build();

		doNothing().when(achievementService).create(anyLong(), eq("HOME"));
		// when & then
		mockMvc.perform(
				post(PATH + "/achievements", communityId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("achievement-create-test",
				preprocessRequest(prettyPrint()),
				requestFields(
					fieldWithPath("content").type(STRING).description("달성 업적")
				)
			));
	}
}
