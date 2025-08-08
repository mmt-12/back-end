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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.memento.server.api.controller.community.CommunityController;
import com.memento.server.api.controller.community.dto.CommunityListResponse.CommunityResponse;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.api.service.community.CommunityService;

public class CommunityControllerDocsTest extends RestDocsSupport {

	private final CommunityService communityService = mock(CommunityService.class);

	@Override
	protected Object initController() {
		return new CommunityController(communityService);
	}

	@Test
	@DisplayName("그룹 목록 조회")
	void searchAll() throws Exception {
		// given
		setAuthentication(1L, null, null);
		when(communityService.searchAll(any())).thenReturn(
			List.of(
				CommunityResponse.builder()
					.id(1L)
					.name("SSAFY 12기 12반")
					.associateId(1L)
					.build(),
				CommunityResponse.builder()
					.id(2L)
					.name("SSAFY 13기 12반")
					.associateId(5L)
					.build()
			)
		);

		// when & then
		mockMvc.perform(get("/api/v1/communities"))
			.andExpect(status().isOk())
			.andDo(document("community-list-test",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					subsectionWithPath("communities").description("그룹 목록"),
					fieldWithPath("communities[].id").description("그룹 아이디"),
					fieldWithPath("communities[].name").description("그룹 이름"),
					fieldWithPath("communities[].associateId").description("그룹 내 자신의 아이디")
				)
			));
	}
}
