package com.memento.server.spring.api.controller.voice;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.spring.ControllerTestSupport;

public class VoiceControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("보이스를 등록한다.")
	void createVoice() throws Exception {
		long groupId = 1L;

		MockMultipartFile voiceFile = new MockMultipartFile(
			"voice", "voice.wav", "audio/wav", "dummy-audio".getBytes()
		);

		String json = objectMapper.writeValueAsString(
			VoiceCreateRequest.builder()
				.name("테스트보이스")
				.voice(voiceFile)
				.build()
		);

		MockMultipartFile requestPart = new MockMultipartFile(
			"request", "request", "application/json", json.getBytes()
		);

		mockMvc.perform(
			multipart("/api/v1/groups/{groupId}/voices", groupId)
				.file(requestPart)
				.file(voiceFile)
				.contentType(MULTIPART_FORM_DATA))
			.andExpect(status().isCreated());
	}
}
