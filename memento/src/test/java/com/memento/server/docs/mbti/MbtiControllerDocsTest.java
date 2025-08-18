package com.memento.server.docs.mbti;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.mbti.MbtiController;
import com.memento.server.api.controller.mbti.dto.CreateMbtiRequest;
import com.memento.server.api.controller.mbti.dto.SearchMbtiResponse;
import com.memento.server.api.service.mbti.MbtiService;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.domain.mbti.Mbti;

public class MbtiControllerDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates/{associateId}/mbti-tests";

	private final MbtiService mbtiService = mock(MbtiService.class);

	@Override
	protected Object initController() {
		return new MbtiController(mbtiService);
	}

	@Test
	@DisplayName("mbti 등록")
	void createTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

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
	void searchTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

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
				get(PATH, communityId, associateId))
			.andDo(print())
			.andExpect(status().isOk())
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
