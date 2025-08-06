package com.memento.server.docs.mbti;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.mbti.MbtiController;
import com.memento.server.api.controller.mbti.dto.CreateMbtiRequest;
import com.memento.server.api.controller.mbti.dto.ReadMbtiResponse;
import com.memento.server.docs.RestDocsSupport;

public class MbtiControllerTestDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/mbti-tests";

	@Override
	protected Object initController() {
		return new MbtiController();
	}

	@Test
	@DisplayName("mbti 등록")
	void createTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;
		CreateMbtiRequest request = CreateMbtiRequest.builder()
			.mbti("ENTP")
			.build();

		//when & then
		mockMvc.perform(
				post(PATH, communityId, associateId)
					.content(objectMapper.writeValueAsString(request))
					.contentType(APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("mbti-create-test",
				preprocessRequest(prettyPrint()),
				requestFields(
					fieldWithPath("mbti").type(STRING).description("mbti")
				)
			));
	}

	@Test
	@DisplayName("mbti 조회")
	void readTest() throws Exception {
		// given
		Long communityId = 1L;
		Long associateId = 1L;

		//when & then
		ReadMbtiResponse response = ReadMbtiResponse.builder()
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

		mockMvc.perform(
				get(PATH, communityId, associateId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.INFP").value(response.INFP()))
			.andExpect(jsonPath("$.INFJ").value(response.INFJ()))
			.andExpect(jsonPath("$.INTP").value(response.INTP()))
			.andExpect(jsonPath("$.INTJ").value(response.INTJ()))
			.andExpect(jsonPath("$.ISFP").value(response.ISFP()))
			.andExpect(jsonPath("$.ISFJ").value(response.ISFJ()))
			.andExpect(jsonPath("$.ISTP").value(response.ISTP()))
			.andExpect(jsonPath("$.ISTJ").value(response.ISTJ()))
			.andExpect(jsonPath("$.ENFP").value(response.ENFP()))
			.andExpect(jsonPath("$.ENFJ").value(response.ENFJ()))
			.andExpect(jsonPath("$.ENTP").value(response.ENTP()))
			.andExpect(jsonPath("$.ENTJ").value(response.ENTJ()))
			.andExpect(jsonPath("$.ESFP").value(response.ESFP()))
			.andExpect(jsonPath("$.ESFJ").value(response.ESFJ()))
			.andExpect(jsonPath("$.ESTP").value(response.ESTP()))
			.andExpect(jsonPath("$.ESTJ").value(response.ESTJ()))
			.andDo(document("mbti-read-test",
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("INFP").type(NUMBER).description("INFP"),
					fieldWithPath("INFJ").type(NUMBER).description("INFJ"),
					fieldWithPath("INTP").type(NUMBER).description("INTP"),
					fieldWithPath("INTJ").type(NUMBER).description("INTJ"),
					fieldWithPath("ISFP").type(NUMBER).description("ISFP"),
					fieldWithPath("ISFJ").type(NUMBER).description("ISFJ"),
					fieldWithPath("ISTP").type(NUMBER).description("ISTP"),
					fieldWithPath("ISTJ").type(NUMBER).description("ISTJ"),
					fieldWithPath("ENFP").type(NUMBER).description("ENFP"),
					fieldWithPath("ENFJ").type(NUMBER).description("ENFJ"),
					fieldWithPath("ENTP").type(NUMBER).description("ENTP"),
					fieldWithPath("ENTJ").type(NUMBER).description("ENTJ"),
					fieldWithPath("ESFP").type(NUMBER).description("ESFP"),
					fieldWithPath("ESFJ").type(NUMBER).description("ESFJ"),
					fieldWithPath("ESTP").type(NUMBER).description("ESTP"),
					fieldWithPath("ESTJ").type(NUMBER).description("ESTJ")
				)
				));
	}
}
