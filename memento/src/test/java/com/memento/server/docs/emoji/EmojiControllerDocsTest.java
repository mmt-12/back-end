package com.memento.server.docs.emoji;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.emoji.EmojiController;
import com.memento.server.api.controller.emoji.dto.request.EmojiCreateRequest;
import com.memento.server.api.service.emoji.EmojiService;
import com.memento.server.docs.RestDocsSupport;

public class EmojiControllerDocsTest extends RestDocsSupport {

	private final EmojiService emojiService = Mockito.mock(EmojiService.class);

	@Override
	protected Object initController() {
		return new EmojiController(emojiService);
	}

	@Test
	@DisplayName("이모지 리액션을 등록한다.")
	void createEmoji() throws Exception {
		// given
		long groupId = 1L;

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

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/emoji", groupId)
					.file(data)
					.file(emoji)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("emoji-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("groupId").description("그룹 ID")
				),
				requestParts(
					partWithName("data").description("이모지 생성 요청 본문 (JSON)"),
					partWithName("emoji").description("이모지 음성 파일 (WAV 형식)")
				),
				requestPartFields("data",
					fieldWithPath("name").type(STRING).description("이모지 이름")
				)
			));
	}
}
