package com.memento.server.spring.api.controller.voice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.memento.server.api.controller.voice.dto.request.VoiceCreateRequest;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.memento.server.common.exception.MementoException;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.spring.api.controller.ControllerTestSupport;
import com.memento.server.voice.VoiceFixtures;

public class VoiceControllerTest extends ControllerTestSupport {

	@Test
	@DisplayName("보이스 리액션을 생성한다.")
	void createVoice() throws Exception {
		// given
		long communityId = 1L;
		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용").build());

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
			"".getBytes()
		);

		doNothing().when(voiceService).createPermanentVoice(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(data)
					.file(voice)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isCreated());

		verify(fileValidator).validateVoiceFile(any());
		verify(voiceService).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 생성 시 name은 필수값이다.")
	void createVoiceWithoutName() throws Exception {
		// given
		long communityId = 1L;
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
			"".getBytes()
		);
		doNothing().when(voiceService).createPermanentVoice(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(data)
					.file(voice)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[0].message").value("name 값은 필수입니다."));

		verify(fileValidator, never()).validateVoiceFile(any());
		verify(voiceService, never()).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 생성 시 name은 최대 34자(한글 기준)까지 입력 가능하다.")
	void createVoiceWithTooLongName() throws Exception {
		// given
		long communityId = 1L;
		String tooLongName = "아".repeat(35);

		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name(tooLongName).build());

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
			"".getBytes()
		);

		doNothing().when(voiceService).createPermanentVoice(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(data)
					.file(voice)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1001))
			.andExpect(jsonPath("$.message").value("잘못된 입력"))
			.andExpect(jsonPath("$.errors[0].field").value("name"))
			.andExpect(jsonPath("$.errors[0].message").value("name은 최대 34자(한글 기준)까지 입력 가능합니다."));

		verify(fileValidator, never()).validateVoiceFile(any());
		verify(voiceService, never()).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 생성 시 data는 필수값이다.")
	void createVoiceWithoutData() throws Exception {
		// given
		long communityId = 1L;

		MockMultipartFile voice = new MockMultipartFile(
			"voice",
			"voice.wav",
			"audio/wav",
			"".getBytes()
		);

		doNothing().when(voiceService).createPermanentVoice(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(voice)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("data"))
			.andExpect(jsonPath("$.errors[0].message").value("data은(는) 필수입니다."));

		verify(fileValidator, never()).validateVoiceFile(any());
		verify(voiceService, never()).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 생성 시 voice는 필수값이다.")
	void createVoiceWithoutVoice() throws Exception {
		// given
		long communityId = 1L;
		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용").build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		doNothing().when(voiceService).createPermanentVoice(any());

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(data)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(1002))
			.andExpect(jsonPath("$.message").value("필수 요청 part 누락"))
			.andExpect(jsonPath("$.errors[0].field").value("voice"))
			.andExpect(jsonPath("$.errors[0].message").value("voice은(는) 필수입니다."));

		verify(fileValidator, never()).validateVoiceFile(any());
		verify(voiceService, never()).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 생성 시 보이스 파일 크기가 설정된 제한을 초과하면 예외가 발생한다.")
	void createVoiceWithTooLargeFile() throws Exception {
		// given
		long communityId = 1L;
		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용").build());

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
			"content".getBytes()
		);

		doThrow(new MementoException(ErrorCodes.VOICE_FILE_TOO_LARGE))
			.when(fileValidator).validateVoiceFile(voice);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(data)
					.file(voice)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(2007))
			.andExpect(jsonPath("$.message").value("음성 파일 크기는 10MB를 초과할 수 없습니다."));

		verify(fileValidator).validateVoiceFile(voice);
		verify(voiceService, never()).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 생성 시 지원하지 않는 파일 형식으로 보이스를 생성하면 예외가 발생한다.")
	void createVoiceWithInvalidFormat() throws Exception {
		// given
		long communityId = 1L;
		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용").build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile voice = new MockMultipartFile(
			"voice",
			"voice.txt",
			"text/plain", // 지원하지 않는 형식
			"content".getBytes()
		);

		doThrow(new MementoException(ErrorCodes.VOICE_INVALID_FORMAT))
			.when(fileValidator).validateVoiceFile(voice);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(data)
					.file(voice)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(2008))
			.andExpect(jsonPath("$.message").value("지원하지 않는 음성 파일 형식입니다."));

		verify(fileValidator).validateVoiceFile(voice);
		verify(voiceService, never()).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 생성 시 contentType이 null인 파일로 보이스를 생성하면 예외가 발생한다.")
	void createVoiceWithNullContentType() throws Exception {
		// given
		long communityId = 1L;
		String json = objectMapper.writeValueAsString(VoiceCreateRequest.builder()
			.name("인쥐용").build());

		MockMultipartFile data = new MockMultipartFile(
			"data",
			"request",
			"application/json",
			json.getBytes()
		);

		MockMultipartFile voice = new MockMultipartFile(
			"voice",
			"voice.wav",
			null, // null contentType
			"content".getBytes()
		);

		doThrow(new MementoException(ErrorCodes.VOICE_INVALID_FORMAT))
			.when(fileValidator).validateVoiceFile(voice);

		// when & then
		mockMvc.perform(
				multipart("/api/v1/communities/{communityId}/voices", communityId)
					.file(data)
					.file(voice)
					.with(withJwt(1L, 1L, 1L))
					.contentType(MULTIPART_FORM_DATA))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value("BAD_REQUEST"))
			.andExpect(jsonPath("$.code").value(2008))
			.andExpect(jsonPath("$.message").value("지원하지 않는 음성 파일 형식입니다."));

		verify(fileValidator).validateVoiceFile(voice);
		verify(voiceService, never()).createPermanentVoice(any());
	}

	@Test
	@DisplayName("보이스 목록을 조회한다.")
	void getVoices() throws Exception {
		// given
		long communityId = 1L;
		long cursor = 1L;
		String keyword = "인쥐용";
		int size = 10;
		Long nextCursor = cursor + size;
		boolean hasNext = true;

		VoiceResponse voiceResponse = VoiceResponse.of(VoiceFixtures.permanentVoice());
		VoiceListResponse response = VoiceListResponse.of(List.of(voiceResponse), cursor, size, nextCursor, hasNext);

		given(voiceService.getVoices(any(VoiceListQueryRequest.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(
				get("/api/v1/communities/{communityId}/voices", communityId)
					.param("cursor", String.valueOf(cursor))
					.param("size", String.valueOf(size))
					.param("keyword", keyword)
					.with(withJwt(1L, 1L, 1L)))
			.andExpect(status().isOk());

		verify(voiceService).getVoices(any(VoiceListQueryRequest.class));
	}

	@Test
	@DisplayName("보이스를 삭제한다.")
	void removeVoice() throws Exception {
		// given
		long communityId = 1L;
		long voiceId = 1L;

		doNothing().when(voiceService).removeVoice(any(VoiceRemoveRequest.class));

		// when && then
		mockMvc.perform(
				delete("/api/v1/communities/{communityId}/voices/{voiceId}", communityId, voiceId)
					.with(withJwt(1L, 1L, 1L)))
			.andDo(print())
			.andExpect(status().isNoContent());

		verify(voiceService).removeVoice(any(VoiceRemoveRequest.class));
	}
}
