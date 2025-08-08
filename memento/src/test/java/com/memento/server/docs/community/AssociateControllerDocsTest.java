package com.memento.server.docs.community;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.community.AssociateController;
import com.memento.server.api.controller.community.dto.ReadAssociateResponse;
import com.memento.server.api.controller.community.dto.UpdateAssociateRequest;
import com.memento.server.api.controller.community.dto.AssociateListResponse;
import com.memento.server.api.controller.community.dto.AssociateListResponse.AssociateResponse;
import com.memento.server.api.controller.community.dto.AssociateListResponse.AssociateResponse.AchievementResponse;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.api.service.community.AssociateService;

public class AssociateControllerDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates";

	private final AssociateService associateService = mock(AssociateService.class);

	@Override
	protected Object initController() {
		return new AssociateController(associateService);
	}

	@Test
	@DisplayName("associate 조회")
	void readTest() throws Exception {
		// given
		Long communityId = 1L;
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
				get(PATH + "/{associateId}", communityId, associateId))
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
	@DisplayName("그룹 참여자 목록 조회")
	void searchAll() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);
		when(associateService.searchAll(any(), any(), any(), any())).thenReturn(
			AssociateListResponse.builder()
				.communityName("SSAFY 12기 12반")
				.associates(
					List.of(
						AssociateResponse.builder()
							.id(1L)
							.nickname("nickname")
							.imageUrl("https://...")
							.introduction("introduction")
							.achievement(
								AchievementResponse.builder()
									.id(1L)
									.name("achievement name")
									.build()
							)
							.build()
					)
				)
				.cursor(5L)
				.hasNext(true)
				.build()
		);

		// when & then
		mockMvc.perform(get(PATH, 1L)
				.param("keyword", "")
				.param("cursor", "")
				.param("size", ""))
			.andExpect(status().isOk())
			.andDo(document("associate-list-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("그룹 아이디")
				),
				queryParameters(
					parameterWithName("keyword").description("키워드"),
					parameterWithName("cursor").description("커서값"),
					parameterWithName("size").description("검색 크기")
				),
				responseFields(
					fieldWithPath("communityName").description("그룹 이름"),
					subsectionWithPath("associates").description("참여자 목록"),
					fieldWithPath("associates[].id").description("참여자 아이디"),
					fieldWithPath("associates[].nickname").description("참여자 닉네임"),
					fieldWithPath("associates[].imageUrl").description("참여자 프로필 이미지 url"),
					fieldWithPath("associates[].introduction").description("참여자 소개문"),
					subsectionWithPath("associates[].achievement").description("참여자 업적"),
					fieldWithPath("associates[].achievement.id").description("업적 아이디"),
					fieldWithPath("associates[].achievement.name").description("업적 이름"),
					fieldWithPath("cursor").description("다음 커서값"),
					fieldWithPath("hasNext").description("다음 페이지 여부")
				)
			));
	}

	@Test
	@DisplayName("associate 수정 API")
	void updateTest() throws Exception {
		// given
		Long communityId = 1L;

		UpdateAssociateRequest request = UpdateAssociateRequest.builder()
			.profileImageUrl("www.example.com/ohjs")
			.nickname("오준수")
			.achievement(2L)
			.introduction("오준수 뤼전드")
			.build();

		// when & then
		mockMvc.perform(
				put(PATH, communityId)
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