package com.memento.server.spring.api.controller.voice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.spring.ControllerTestSupport;

public class VoiceControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("보이스 리액션을 등록한다.")
	void createVoice() throws Exception {
	    // given
		long groupId = 1L;

		String request = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용")
			.build());

		MockMultipartFile requestPart = new MockMultipartFile(
			"request", "request", "application/json", request.getBytes()
		);

		MockMultipartFile voicePart = new MockMultipartFile(
			"voice", "voice.wav", "audio/wav", "dummy-audio-content".getBytes()
		);

		// when && then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/voices", groupId)
					.file(requestPart)
					.file(voicePart)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());

		verify(voiceService).createVoice(any());
	}
}
