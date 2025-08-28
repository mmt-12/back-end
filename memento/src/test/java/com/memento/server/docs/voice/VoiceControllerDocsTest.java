package com.memento.server.docs.voice;

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
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
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

import com.memento.server.api.controller.voice.VoiceController;
import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.common.dto.response.PageInfo;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.memento.server.common.fixture.CommonFixtures;
import com.memento.server.common.validator.FileValidator;
import com.memento.server.docs.RestDocsSupport;
import com.memento.server.voice.VoiceFixtures;

public class VoiceControllerDocsTest extends RestDocsSupport {

	private final VoiceService voiceService = Mockito.mock(VoiceService.class);
	private final FileValidator fileValidator = Mockito.mock(FileValidator.class);

	@Override
	protected Object initController() {
		return new VoiceController(voiceService, fileValidator);
	}

	@Test
	@DisplayName("보이스 리액션을 생성한다.")
	void createVoice() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		long communityId = 1L;
		MockMultipartFile data = CommonFixtures.jsonFile(VoiceCreateRequest.builder().name("인쥐용").build());
		MockMultipartFile voice = CommonFixtures.voiceFile();

		doNothing().when(voiceService).createPermanentVoice(any());

		// when && then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(data)
					.file(voice)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated())
			.andDo(document("voice-create",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID")
				),
				requestParts(
					partWithName("data").description("보이스 생성 요청 본문 (JSON)"),
					partWithName("voice").description("보이스 음성 파일 (WAV 형식)")
				),
				requestPartFields("data",
					fieldWithPath("name").type(STRING).description("보이스 이름")
				)
			));

		verify(voiceService).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 목록을 조회한다.")
	void getVoices() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		long communityId = 1L;
		long cursor = 1L;
		String keyword = "인쥐용";
		int size = 10;
		Long nextCursor = cursor + size;
		boolean hasNext = true;

		VoiceResponse voiceResponse = VoiceResponse.of(VoiceFixtures.permanentVoice());
		VoiceListResponse response = VoiceListResponse.of(List.of(voiceResponse), PageInfo.of(hasNext, nextCursor));

		given(voiceService.getVoices(any(VoiceListQueryRequest.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(
				get("/api/v1/communities/{communityId}/voices", communityId)
					.param("cursor", String.valueOf(cursor))
					.param("size", String.valueOf(size))
					.param("keyword", keyword))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("voice-get",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID")
				),
				queryParameters(
					parameterWithName("cursor").description("현재 페이지의 마지막 보이스 ID (첫 페이지는 null)").optional(),
					parameterWithName("size").description("요청할 보이스 수 (1-30, 기본값: 10)").optional(),
					parameterWithName("keyword").description("보이스 이름 검색 키워드 (선택)").optional()
				),
				responseFields(
					fieldWithPath("voices[].id").description("보이스 ID"),
					fieldWithPath("voices[].name").description("보이스 이름"),
					fieldWithPath("voices[].url").description("보이스 오디오 URL"),
					fieldWithPath("voices[].author.id").description("보이스 작성자 ID"),
					fieldWithPath("voices[].author.nickname").description("보이스 작성자 닉네임"),
					fieldWithPath("voices[].author.imageUrl").description("보이스 작성자 프로필 이미지 URL"),
					fieldWithPath("pageInfo.hasNext").description("다음 페이지 존재 여부"),
					fieldWithPath("pageInfo.nextCursor").description("다음 페이지 커서 (더 불러올 보이스가 있을 경우)")
				)
			));

		verify(voiceService).getVoices(any(VoiceListQueryRequest.class));
	}

	@Test
	@DisplayName("보이스를 삭제한다.")
	void removeVoice() throws Exception {
		// given
		setAuthentication(1L, 1L, 1L);

		Long communityId = 1L;
		Long voiceId = 1L;

		doNothing().when(voiceService).removeVoice(any(VoiceRemoveRequest.class));

		// when && then
		mockMvc.perform(
				delete("/api/v1/communities/{communityId}/voices/{voiceId}", communityId, voiceId))
			.andDo(print())
			.andExpect(status().isNoContent())
			.andDo(document("voice-remove",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("communityId").description("커뮤니티 ID"),
					parameterWithName("voiceId").description("삭제할 보이스 ID")
				)
			));

		verify(voiceService).removeVoice(any(VoiceRemoveRequest.class));
	}
}
