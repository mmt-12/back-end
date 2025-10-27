package com.memento.server.docs.community;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
import com.memento.server.api.service.community.dto.response.SearchAssociateResponse;
import com.memento.server.api.controller.community.dto.request.UpdateAssociateRequest;
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
	void searchTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long associateId = 1L;

		given(associateService.search(anyLong(), anyLong()))
			.willReturn(
				SearchAssociateResponse.builder()
					.nickname("example")
					.achievement(SearchAssociateResponse.Achievement.builder()
						.id(1L)
						.name("example")
						.build())
					.imageUrl("www.example.com/example")
					.introduction("example introduction")
					.birthday(LocalDate.of(1999, 1, 1))
					.build()
			);

		// when & then
		mockMvc.perform(
				get(PATH + "/{associateId}", communityId, associateId))
			.andDo(print())
			.andExpect(status().isOk())
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
		when(associateService.searchAll(any(), any())).thenReturn(
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
				.build()
		);

		// when & then
		mockMvc.perform(get(PATH, 1L))
			.andExpect(status().isOk())
			.andDo(document("associate-list-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("그룹 아이디")
				),
				queryParameters(
					parameterWithName("keyword").optional().description("키워드")
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
					fieldWithPath("associates[].achievement.name").description("업적 이름")
				)
			));
	}

	@Test
	@DisplayName("associate 수정 API")
	void updateTest() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;

		UpdateAssociateRequest request = UpdateAssociateRequest.builder()
			.profileImageUrl("https://...")
			.nickname("nickname")
			.achievement(1L)
			.introduction("introduction")
			.build();
		doNothing().when(associateService).update(anyLong(), anyLong(), anyString(), anyString(), anyLong(), anyString());

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