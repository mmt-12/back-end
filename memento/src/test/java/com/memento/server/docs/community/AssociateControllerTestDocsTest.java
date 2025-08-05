package com.memento.server.docs.community;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.controller.community.AssociateController;
import com.memento.server.controller.community.AssociateListResponse;
import com.memento.server.controller.community.AssociateListResponse.AssociateResponse;
import com.memento.server.controller.community.AssociateListResponse.AssociateResponse.AchievementResponse;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.service.community.AssociateService;

public class AssociateControllerTestDocsTest extends RestDocsSupport {

	public static final String PATH = "/api/v1/communities/{communityId}/associates";

	private final AssociateService associateService = mock(AssociateService.class);

	@Override
	protected Object initController() {
		return new AssociateController(associateService);
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
}
