package com.memento.server.docs.community;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.community.AssociateController;
import com.memento.server.api.controller.community.dto.ReadAssociateResponse;
import com.memento.server.api.controller.community.dto.UpdateAssociateRequest;
import com.memento.server.docs.RestDocsSupport;

public class AssociateControllerTestDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/groups/{groupId}/associates";
	@Override
	protected Object initController() {
		return new AssociateController();
	}

	@Test
	@DisplayName("associate 조회 API")
	void readTest() throws Exception {
		// given
		Long groupId = 1L;
		Long associateId = 1L;

		// when & then
		ReadAssociateResponse response = ReadAssociateResponse.builder()
			.nickname("오큰수")
			.achievement(ReadAssociateResponse.Achievement.builder()
				.id(1L)
				.name("뤼전드")
				.build())
			.imageUrl("www.example.com/ohjs")
			.introduction("싱싱싱~ 팅!팅!팅! 아!다 막았죠! 인지용~?")
			.birthday(LocalDate.of(1999, 10, 13))
			.build();

		mockMvc.perform(
				get(PATH + "/{associateId}", groupId, associateId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.nickname").value(response.nickname()))
			.andExpect(jsonPath("$.achievement.id").value(response.achievement().getId()))
			.andExpect(jsonPath("$.achievement.name").value(response.achievement().getName()))
			.andExpect(jsonPath("$.imageUrl").value(response.imageUrl()))
			.andExpect(jsonPath("$.introduction").value(response.introduction()))
			.andExpect(jsonPath("$.birthday").value(response.birthday().toString()))
			.andDo(document("associate-read-test",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("nickname").type(STRING).description("이름"),
					fieldWithPath("achievement").type(OBJECT).description("업적 ID"),
					fieldWithPath("achievement.id").type(NUMBER).description("업적 ID"),
					fieldWithPath("achievement.name").type(STRING).description("업적 이름"),
					fieldWithPath("imageUrl").type(STRING).description("프로필 이미지"),
					fieldWithPath("introduction").type(STRING).description("한 줄 설명"),
					fieldWithPath("birthday").type(STRING).description("생일")
				)
			));
	}

	@Test
	@DisplayName("associate 수정 API")
	void updateTest() throws Exception {
		// given
		Long groupId = 1L;

		UpdateAssociateRequest request = UpdateAssociateRequest.builder()
			.profileImageUrl("www.example.com/ohjs")
			.nickname("오준수")
			.achievement(2L)
			.introduction("오준수 뤼전드")
			.build();

		// when & then
		mockMvc.perform(
			put(PATH, groupId)
				.content(objectMapper.writeValueAsString(request))
				.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("associate-update-test",
				preprocessRequest(prettyPrint()),
				requestFields(
					fieldWithPath("profileImageUrl").type(STRING).description("프로필 이미지"),
					fieldWithPath("nickname").type(STRING).description("이름"),
					fieldWithPath("achievement").type(NUMBER).description("업적"),
					fieldWithPath("introduction").type(STRING).description("한 줄 소개")
				)
			));
	}
}
