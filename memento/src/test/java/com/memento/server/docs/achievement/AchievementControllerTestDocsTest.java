package com.memento.server.docs.achievement;

import static java.sql.JDBCType.ARRAY;
import static java.sql.JDBCType.BOOLEAN;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.achievement.AchievementController;
import com.memento.server.docs.RestDocsSupport;

public class AchievementControllerTestDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/achievements";

	@Override
	protected Object initController() {
		return new AchievementController();
	}

	@Test
	@DisplayName("업적 조회 API")
	void readTest() throws Exception {
		// given
		Long groupId = 1L;
		Long associateId = 1L;

		// when & then
		mockMvc.perform(
				get(PATH, groupId, associateId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.achievements[0].id").value(1))
			.andExpect(jsonPath("$.achievements[0].name").value("뤼전드"))
			.andExpect(jsonPath("$.achievements[0].criteria").value("오준수 전용"))
			.andExpect(jsonPath("$.achievements[0].obtained").value(true))
			.andExpect(jsonPath("$.achievements[0].type").value("HIDDEN"))
			.andExpect(jsonPath("$.achievements[1].id").value(2))
			.andExpect(jsonPath("$.achievements[1].name").value("GMG"))
			.andExpect(jsonPath("$.achievements[1].criteria").value("기억 다수 참여"))
			.andExpect(jsonPath("$.achievements[1].obtained").value(true))
			.andExpect(jsonPath("$.achievements[1].type").value("OPEN"))
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
}
