package com.memento.server.spring.api.controller.voice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.memento.server.fixture.voice.VoiceFixtures;
import com.memento.server.spring.ControllerTestSupport;

public class VoiceControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("보이스 리액션을 등록한다.")
	void createVoice() throws Exception {
		// given
		long groupId = 1L;

		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용")
			.build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile voice = new MockMultipartFile(
			"voice",
			"voice.wav",
			"audio/wav",
			"dummy-audio-content".getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/voices", groupId)
					.file(data)
					.file(voice)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("보이스를 등록할 때, name은 필수값이다.")
	void createVoiceWithoutName() throws Exception {
		// given
		long groupId = 1L;

		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder().build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile voice = new MockMultipartFile(
			"voice",
			"voice.wav",
			"audio/wav",
			"dummy-audio-content".getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/voices", groupId)
					.file(data)
					.file(voice)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[0].message").value("name 값은 필수입니다."));
	}

	@Test
	@DisplayName("보이스를 등록할 때, name은 최대 34자(한글 기준)까지 입력 가능하다.")
	void createVoiceWithTooLongName() throws Exception {
		// given
		long groupId = 1L;

		String tooLongName = "가".repeat(35);

		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder().name(tooLongName).build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile voice = new MockMultipartFile(
			"voice",
			"voice.wav",
			"audio/wav",
			"dummy-audio-content".getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/voices", groupId)
					.file(data)
					.file(voice)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[0].message").value("name은 최대 34자(한글 기준)까지 입력 가능합니다."));
	}

	@Test
	@DisplayName("보이스를 등록할 때, data는 필수값이다.")
	void createVoiceWithoutData() throws Exception {
		// given
		long groupId = 1L;

		MockMultipartFile voice = new MockMultipartFile(
			"voice",
			"voice.wav",
			"audio/wav",
			"dummy-audio-content".getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/voices", groupId)
					.file(voice)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("data"))
			.andExpect(jsonPath("$.errors[0].message").value("data은(는) 필수입니다."));
	}

	@Test
	@DisplayName("보이스를 등록할 때, voice는 필수값이다.")
	void createVoiceWithoutVoice() throws Exception {
		// given
		long groupId = 1L;

		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용")
			.build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/groups/{groupId}/voices", groupId)
					.file(data)
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("voice"))
			.andExpect(jsonPath("$.errors[0].message").value("voice은(는) 필수입니다."));
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
			.andExpect(jsonPath("$.hasNext").value(response.hasNext()));
	}

	@Test
	@DisplayName("등록된 보이스를 삭제한다.")
	void removeVoice() throws Exception {
		// given
		Long groupId = 1L;
		Long voiceId = 1L;

		doNothing().when(voiceService).removeVoice(any(VoiceRemoveRequest.class));

		// when && then
		mockMvc.perform(
				delete("/api/v1/groups/{groupId}/voices/{voiceId}", groupId, voiceId))
			.andDo(print())
			.andExpect(status().isNoContent());
	}
}
