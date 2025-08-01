package com.memento.server.docs.voice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.voice.VoiceController;
import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.docs.RestDocsSupport;

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
				requestParts(
					partWithName("data").description("보이스 생성 요청 정보 (JSON)"),
					partWithName("voice").description("업로드할 음성 파일")
				),
				requestPartFields("data",
					fieldWithPath("name").type(STRING).description("보이스 이름")
				)
			));

		verify(voiceService).createVoice(any());
	}
}
