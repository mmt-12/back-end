package com.memento.server.docs.voice;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.voice.VoiceController;
import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.fixture.voice.VoiceFixtures;

public class VoiceControllerDocsTest extends RestDocsSupport {

	private final VoiceService voiceService = Mockito.mock(VoiceService.class);

	@Override
	protected Object initController() {
		return new VoiceController(voiceService);
	}

	@Test
	@DisplayName("보이스 리액션을 등록한다.")
	void createVoice() throws Exception {
		// given
		long groupId = 1L;

		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용")
			.build());

		MockMultipartFile data = new MockMultipartFile(
			"data", "request", "application/json", json.getBytes()
		);

		MockMultipartFile voice = new MockMultipartFile(
			"voice", "voice.wav", "audio/wav", "dummy-audio-content".getBytes()
		);

		// when && then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/voices", groupId)
					.file(data)
					.file(voice)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("voice-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("groupId").description("보이스를 생성할 그룹 ID")
				),
				requestParts(
					partWithName("data").description("보이스 생성 요청 본문 (JSON)"),
					partWithName("voice").description("보이스 음성 파일 (WAV 형식)")
				),
				requestPartFields("data",
					fieldWithPath("name").type(STRING).description("보이스 이름")
				)
			));
	}

	@Test
	@DisplayName("등록된 보이스 목록을 조회한다.")
	void getVoices() throws Exception {
		// given
		Long groupId = 1L;
		Long cursor = 1L;
		String keyword = "인쥐용";
		int size = 10;
		Long nextCursor = cursor + size;
		boolean hasNext = true;

		VoiceListResponse response = VoiceFixtures.voiceListResponse(cursor, size, nextCursor, hasNext);
		VoiceResponse voice = response.voices().get(0);

		when(voiceService.getVoices(VoiceListQueryRequest.of(groupId, cursor, size, keyword))).thenReturn(response);

		// when & then
		mockMvc.perform(
				get("/api/v1/groups/{groupId}/voices", groupId)
					.param("cursor", cursor.toString())
					.param("size", String.valueOf(size))
					.param("keyword", keyword))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.voices").isArray())
			.andExpect(jsonPath("$.voices[0].id").value(voice.id()))
			.andExpect(jsonPath("$.voices[0].name").value(voice.name()))
			.andExpect(jsonPath("$.voices[0].url").value(voice.url()))
			.andExpect(jsonPath("$.voices[0].author.id").value(voice.author().id()))
			.andExpect(jsonPath("$.voices[0].author.nickname").value(voice.author().nickname()))
			.andExpect(jsonPath("$.voices[0].author.imageUrl").value(voice.author().imageUrl()))
			.andExpect(jsonPath("$.cursor").value(response.cursor()))
			.andExpect(jsonPath("$.size").value(response.size()))
			.andExpect(jsonPath("$.nextCursor").value(response.nextCursor()))
			.andExpect(jsonPath("$.hasNext").value(response.hasNext()))
			.andDo(document("voice-get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("groupId").description("보이스를 조회할 그룹 ID")
				),
				queryParameters(
					parameterWithName("cursor").description("현재 페이지의 마지막 보이스 ID (첫 페이지는 null)").optional(),
					parameterWithName("size").description("요청할 보이스 수 (기본값: 10)").optional(),
					parameterWithName("keyword").description("보이스 이름 검색 키워드 (선택)").optional()
				),
				responseFields(
					fieldWithPath("voices[].id").description("보이스 ID"),
					fieldWithPath("voices[].name").description("보이스 이름"),
					fieldWithPath("voices[].url").description("보이스 오디오 URL"),
					fieldWithPath("voices[].author.id").description("보이스 작성자 ID"),
					fieldWithPath("voices[].author.nickname").description("보이스 작성자 닉네임"),
					fieldWithPath("voices[].author.imageUrl").description("보이스 작성자 프로필 이미지 URL"),
					fieldWithPath("cursor").description("현재 커서 위치 (마지막으로 조회한 보이스 ID)"),
					fieldWithPath("size").description("요청한 보이스 수"),
					fieldWithPath("nextCursor").description("다음 페이지 커서 (더 불러올 보이스가 있을 경우)"),
					fieldWithPath("hasNext").description("다음 페이지 존재 여부")
				)
			));
	}
}
