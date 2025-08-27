package com.memento.server.docs.emoji;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.emoji.EmojiController;
import com.memento.server.api.controller.emoji.dto.request.EmojiCreateRequest;
import com.memento.server.api.service.emoji.EmojiService;
import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiRemoveRequest;
import com.memento.server.api.service.emoji.dto.response.EmojiListResponse;
import com.memento.server.api.service.emoji.dto.response.EmojiResponse;
import com.memento.server.common.dto.response.PageInfo;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.emoji.EmojiFixtures;

public class EmojiControllerDocsTest extends RestDocsSupport {

	private final EmojiService emojiService = Mockito.mock(EmojiService.class);

	@Override
	protected Object initController() {
		return new EmojiController(emojiService);
	}

	@Test
	@DisplayName("이모지 리액션을 생성한다.")
	void createEmoji() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		long communityId = 1L;
		String json = objectMapper.writeValueAsString(EmojiCreateRequest.builder()
			.name("인쥐용")
			.build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile emoji = new MockMultipartFile(
			"emoji",
			"emoji.png",
			"image/png",
			new byte[] {(byte) 0x89, 'P', 'N', 'G'}
		);

		doNothing().when(emojiService).createEmoji(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/emoji", communityId)
					.file(data)
					.file(emoji)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("emoji-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID")
				),
				requestParts(
					partWithName("data").description("이모지 생성 요청 본문 (JSON)"),
					partWithName("emoji").description("이모지 음성 파일 (WAV 형식)")
				),
				requestPartFields("data",
					fieldWithPath("name").type(STRING).description("이모지 이름")
				)
			));

		verify(emojiService).createEmoji(any());
	}

	@Test
	@DisplayName("이모지 목록을 조회한다.")
	void getEmoji() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		long communityId = 1L;
		long cursor = 1L;
		String keyword = "인쥐용";
		int size = 10;
		Long nextCursor = cursor + size;
		boolean hasNext = true;

		EmojiResponse emojiResponse = EmojiResponse.of(EmojiFixtures.emoji());
		EmojiListResponse response = EmojiListResponse.of(List.of(emojiResponse), PageInfo.of(hasNext, nextCursor));

		given(emojiService.getEmoji(any(EmojiListQueryRequest.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(
				get("/api/v1/communities/{communityId}/emoji", communityId)
					.param("cursor", String.valueOf(cursor))
					.param("size", String.valueOf(size))
					.param("keyword", keyword))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("emoji-get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID")
				),
				queryParameters(
					parameterWithName("cursor").description("현재 페이지의 마지막 이모지 ID (첫 페이지는 null)").optional(),
					parameterWithName("size").description("요청할 이모지 수 (기본값: 10)").optional(),
					parameterWithName("keyword").description("이모지 이름 검색 키워드 (선택)").optional()
				),
				responseFields(
					fieldWithPath("emoji[].id").description("이모지 ID"),
					fieldWithPath("emoji[].name").description("이모지 이름"),
					fieldWithPath("emoji[].url").description("이모지 오디오 URL"),
					fieldWithPath("emoji[].author.id").description("이모지 작성자 ID"),
					fieldWithPath("emoji[].author.nickname").description("이모지 작성자 닉네임"),
					fieldWithPath("emoji[].author.imageUrl").description("이모지 작성자 프로필 이미지 URL"),
					fieldWithPath("pageInfo.hasNext").description("다음 페이지 존재 여부"),
					fieldWithPath("pageInfo.nextCursor").description("다음 페이지 커서 (더 불러올 보이스가 있을 경우)")
				)
			));

		verify(emojiService).getEmoji(any(EmojiListQueryRequest.class));
	}

	@Test
	@DisplayName("이모지를 삭제한다.")
	void removeEmoji() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		long communityId = 1L;
		long emojiId = 1L;

		doNothing().when(emojiService).removeEmoji(any(EmojiRemoveRequest.class));

		// when && then
		mockMvc.perform(
				delete("/api/v1/communities/{communityId}/emoji/{emojiId}", communityId, emojiId))
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(document("emoji-remove",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID"),
					parameterWithName("emojiId").description("삭제할 이모지 ID")
				)
			));

		verify(emojiService).removeEmoji(any(EmojiRemoveRequest.class));
	}
}
